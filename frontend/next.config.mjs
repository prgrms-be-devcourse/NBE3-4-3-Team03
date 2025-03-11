/** @type {import('next').NextConfig} */
const nextConfig = {
    async rewrites() {
        return [
            {
                source: '/sse/:path*',
                destination: 'http://localhost:8080/sse/:path*'
            },
        ];
    },
    // Node.js 경고 메시지 숨기기
    webpack: (config, { isServer }) => {
        if (!isServer) {
            config.resolve.fallback = Object.assign({}, config.resolve.fallback, {
                util: false
            });
        }
        return config;
    },
};

export default nextConfig;
