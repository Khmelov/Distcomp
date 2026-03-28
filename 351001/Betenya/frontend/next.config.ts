import type { NextConfig } from 'next'

const nextConfig: NextConfig = {
    reactStrictMode: true,
    // swcMinify: true,
    experimental: {
        // turbo: {
        //     rules: {
        //         '*.svg': {
        //             loaders: ['@svgr/webpack'],
        //             as: '*.js',
        //         },
        //     },
        // },
    },
    images: {
        domains: ['example.com'],
    },
}

export default nextConfig