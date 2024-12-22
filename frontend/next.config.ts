import type { NextConfig } from "next";

const nextConfig: NextConfig = {
    images: {
        remotePatterns: [
            {
                protocol: 'https',
                hostname: 'ecommerce-java.s3.ap-southeast-1.amazonaws.com',
                port: '',
                pathname: '/**',
            }
        ]
    },
    eslint: {
        ignoreDuringBuilds: true
    },
};

export default nextConfig;
