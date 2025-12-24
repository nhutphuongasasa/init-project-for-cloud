"use client"

import React from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Package, ShoppingCart, Warehouse, DollarSign } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Header } from "@/components/custom/header"
import api from "@/lib/axios"

// Interfaces
interface VendorProfile {
  address: string
  email: string
  phone: string
  taxCode: string
  websiteUrl: string
}

interface VendorData {
  id: string
  name: string
  description: string
  logoUrl: string
  slug: string
  status: string
  joinedAt: string
  profile: VendorProfile
}

interface InventoryItem {
  productName: string
  quantityAvailable?: number
  quantity?: number
}

interface ApiResponse<T> {
  data: T
  message: string
  timestamp: string
}

export default function VendorDashboard() {
  const [vendorData, setVendorData] = React.useState<VendorData | null>(null)
  const [inventoryData, setInventoryData] = React.useState<InventoryItem[]>([])
  const [loading, setLoading] = React.useState<boolean>(true)

  React.useEffect(() => {
    fetchVendorData()
  }, [])

  const fetchVendorData = async () => {
    try {
      const response = await api.get('/api/vendor/vendor-registration/me', {
        withCredentials: true,
      })
      console.log('Vendor API response:', response.data);
      const result: ApiResponse<VendorData> = response.data
      if (result.data) {
        setVendorData(result.data)
      }
    } catch (error) {
      console.error('Error fetching vendor data:', error)
      // Fallback mock data
      setVendorData({
        id: "c8e5af8e-dff0-4ff4-8156-47d4c13a0a6b",
        name: "Vendor Demo",
        description: "This is a demo vendor for testing",
        logoUrl: "https://example.com/logo.png",
        slug: "vendor-demo",
        status: "ACTIVE",
        joinedAt: "2025-12-16T10:33:21.630992Z",
        profile: {
          address: "123 ABC Street, Ho Chi Minh City",
          email: "demo@vendor.com",
          phone: "0909123456",
          taxCode: "TAX123456",
          websiteUrl: "https://vendor-demo.com"
        }
      })
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return (
      <div className="w-full h-screen flex items-center justify-center">
        <div className="text-muted-foreground">Loading...</div>
      </div>
    )
  }

  return (
    <div className="w-full">
      <Header 
        title="Dashboard" 
        subtitle={vendorData ? `Welcome back, ${vendorData.name}` : "Welcome to Seller Portal"} 
      />

      <div className="p-6 space-y-6">
        {/* Vendor Information */}
        {vendorData && (
          <Card>
            <CardHeader>
              <CardTitle>Vendor Information</CardTitle>
              <CardDescription>Your business profile</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="flex items-start gap-4">
                  {vendorData.logoUrl && (
                    <img 
                      src={vendorData.logoUrl} 
                      alt={vendorData.name}
                      className="w-16 h-16 rounded-lg object-cover border"
                      onError={(e) => e.currentTarget.style.display = 'none'}
                    />
                  )}
                  <div>
                    <h3 className="text-lg font-semibold">{vendorData.name}</h3>
                    <p className="text-sm text-muted-foreground mt-1">{vendorData.description}</p>
                    <div className="mt-2 flex items-center gap-3">
                      <span className={`px-3 py-1 text-xs rounded-full ${
                        vendorData.status === 'ACTIVE' 
                          ? 'bg-green-500/20 text-green-600' 
                          : 'bg-red-500/20 text-red-600'
                      }`}>
                        {vendorData.status}
                      </span>
                      <span className="text-xs text-muted-foreground">
                        Joined: {new Date(vendorData.joinedAt).toLocaleDateString()}
                      </span>
                    </div>
                  </div>
                </div>

                <div className="space-y-3 text-sm">
                  <div className="flex gap-2">
                    <span className="text-muted-foreground w-20">Email:</span>
                    <span>{vendorData.profile.email}</span>
                  </div>
                  <div className="flex gap-2">
                    <span className="text-muted-foreground w-20">Phone:</span>
                    <span>{vendorData.profile.phone}</span>
                  </div>
                  <div className="flex gap-2">
                    <span className="text-muted-foreground w-20">Address:</span>
                    <span>{vendorData.profile.address}</span>
                  </div>
                  <div className="flex gap-2">
                    <span className="text-muted-foreground w-20">Tax Code:</span>
                    <span>{vendorData.profile.taxCode}</span>
                  </div>
                  {vendorData.profile.websiteUrl && (
                    <div className="flex gap-2">
                      <span className="text-muted-foreground w-20">Website:</span>
                      <a href={vendorData.profile.websiteUrl} target="_blank" rel="noopener noreferrer" className="text-primary hover:underline">
                        {vendorData.profile.websiteUrl}
                      </a>
                    </div>
                  )}
                </div>
              </div>
            </CardContent>
          </Card>
        )}

        {/* Basic KPI Cards */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Total Revenue</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex items-center justify-between">
                <div className="text-2xl font-bold">$24,520</div>
                <DollarSign className="h-8 w-8 text-muted-foreground opacity-30" />
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Active Products</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex items-center justify-between">
                <div className="text-2xl font-bold">47</div>
                <Package className="h-8 w-8 text-muted-foreground opacity-30" />
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Pending Orders</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex items-center justify-between">
                <div className="text-2xl font-bold">8</div>
                <ShoppingCart className="h-8 w-8 text-yellow-600 opacity-30" />
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Total Stock</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex items-center justify-between">
                <div className="text-2xl font-bold">1,847</div>
                <Warehouse className="h-8 w-8 text-muted-foreground opacity-30" />
              </div>
            </CardContent>
          </Card>
        </div>

      </div>
    </div>
  )
}