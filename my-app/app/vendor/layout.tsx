"use client"

import { Sidebar } from "@/components/custom/sidebar"
import type React from "react"

// import { Sidebar } from "@/components/sidebar"
import { useState, useEffect } from "react"

export default function VendorLayout({
  children,
}: {
  children: React.ReactNode
}) {
  const [mounted, setMounted] = useState(false)

  useEffect(() => {
    setMounted(true)
  }, [])

  if (!mounted) return null

  return (
    <div className="flex h-screen bg-background">
      <Sidebar userType="vendor" />
      <main className="flex-1 overflow-auto ml-20 md:ml-0 mt-16 md:mt-0">{children}</main>
    </div>
  )
}
