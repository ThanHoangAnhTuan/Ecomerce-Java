FROM jenkins/jenkins:lts

USER root

# Cài đặt các công cụ cần thiết
RUN apt-get update -qq && apt-get install -qqy \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg2 \
    software-properties-common

# Thêm khóa GPG của Docker và Docker repository
RUN curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add - \
    && add-apt-repository \
    "deb [arch=amd64] https://download.docker.com/linux/debian \
    $(lsb_release -cs) stable" \
    && apt-get update -qq

# Cài đặt Docker CE
RUN apt-get install -y docker-ce

# Thêm user Jenkins vào nhóm Docker
RUN usermod -aG docker jenkins

# Đặt biến môi trường để sử dụng Docker (chỉ cần cho Windows)
ENV DOCKER_HOST=tcp://host.docker.internal:2375

USER jenkins
