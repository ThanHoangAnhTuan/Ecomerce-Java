# Sử dụng Jenkins chính thức
FROM jenkins/jenkins:lts

# Đảm bảo chạy với quyền root để cài đặt thêm công cụ
USER root

# Cài đặt các công cụ cần thiết: Ansible, Docker CLI và Socat
RUN apt-get update && \
    apt-get install -y software-properties-common && \
    apt-add-repository --yes --update ppa:ansible/ansible && \
    apt-get install -y ansible docker.io socat && \
    apt-get clean

# Thêm Jenkins user vào nhóm Docker để Jenkins có thể chạy Docker CLI
RUN usermod -aG docker jenkins

# Cấu hình môi trường để kết nối tới Docker Host qua Socat
ENV DOCKER_HOST=tcp://host.docker.internal:2375

# Chuyển về user Jenkins
USER jenkins
