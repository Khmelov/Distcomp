import type { NextConfig } from 'next'

const nextConfig: NextConfig = {
    reactStrictMode: true,
    output: 'standalone',
    images: {
        domains: ['example.com'],
    },
}

export default nextConfig