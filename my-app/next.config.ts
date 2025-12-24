import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  trailingSlash: true,
  images: {
    unoptimized: true,  // Vì next export không hỗ trợ next/image optimization
  },
  output: "standalone",
  // images: {
  //   remotePatterns: [
  //     {
  //       protocol: "https",
  //       hostname: "project-okella.s3.us-east-2.amazonaws.com",
  //       pathname: "/**", // cho phép toàn bộ ảnh trong bucket
  //     },
  //   ],
  // },
};

export default nextConfig;
