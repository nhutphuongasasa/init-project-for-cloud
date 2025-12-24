"use client"

import { Sidebar } from "@/components/custom/sidebar"
import { usePathname } from "next/navigation"
import type React from "react"

import { useState, useEffect } from "react"

export default function VendorLayout({
  children,
}: {
  children: React.ReactNode
}) {
  const [mounted, setMounted] = useState(false)
  const pathname = usePathname()

  useEffect(() => {
    setMounted(true)
  }, [])

  if (!mounted) return null

  const normalizedPathname = pathname.endsWith('/') && pathname !== '/' 
      ? pathname.slice(0, -1) 
      : pathname;

  const noSideBarList = [
      '/vendor/register',
  ];

  const shouldShowSidebar = !noSideBarList.includes(normalizedPathname);

  return (
    <div className={`flex h-screen bg-background ${shouldShowSidebar ? '' : 'items-center justify-center'}`}>
      {shouldShowSidebar && <Sidebar userType="vendor" />}

      <main className={
        shouldShowSidebar 
          ? 'flex-1 overflow-auto ml-20 md:ml-0 mt-16 md:mt-0' 
          : 'w-full max-w-2xl p-6'  // Khi không có sidebar: full màn hình đẹp cho form
      }>
        {children}
      </main>
    </div>
  )
}
