"use client"

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Header } from "@/components/custom/header"
import { Package, Truck, Users, Inbox, CheckCircle, Warehouse, Settings, ShoppingCart } from "lucide-react"

export default function AdminDashboardPage() {
  return (
    <div className="min-h-screen bg-gray-50">
      <Header title="Dashboard" subtitle="Welcome to your admin control center" />

      <div className="p-6 space-y-10">
        {/* Welcome section */}
        <div className="text-center py-16">
          <h1 className="text-5xl font-bold text-foreground mb-6">
            Hello, Admin! 👋
          </h1>
          <p className="text-xl text-muted-foreground max-w-3xl mx-auto leading-relaxed">
            This is your modern administration space.<br />
            All modules are ready and running smoothly.
          </p>
        </div>

        {/* Main feature cards - icons + short descriptions only */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
          <Card className="border-border hover:shadow-xl transition-all duration-300 hover:-translate-y-1">
            <CardHeader className="flex flex-row items-center gap-4">
              <div className="p-3 rounded-lg bg-blue-100">
                <ShoppingCart className="w-6 h-6 text-blue-700" />
              </div>
              <CardTitle className="text-lg">Order Management</CardTitle>
            </CardHeader>
            <CardContent>
              <CardDescription>
                Easily track and process outbound and inbound orders
              </CardDescription>
            </CardContent>
          </Card>

          <Card className="border-border hover:shadow-xl transition-all duration-300 hover:-translate-y-1">
            <CardHeader className="flex flex-row items-center gap-4">
              <div className="p-3 rounded-lg bg-green-100">
                <Warehouse className="w-6 h-6 text-green-700" />
              </div>
              <CardTitle className="text-lg">Inventory Management</CardTitle>
            </CardHeader>
            <CardContent>
              <CardDescription>
                Control stock levels, storage locations, and goods movement
              </CardDescription>
            </CardContent>
          </Card>

          <Card className="border-border hover:shadow-xl transition-all duration-300 hover:-translate-y-1">
            <CardHeader className="flex flex-row items-center gap-4">
              <div className="p-3 rounded-lg bg-purple-100">
                <Users className="w-6 h-6 text-purple-700" />
              </div>
              <CardTitle className="text-lg">Customers & Suppliers</CardTitle>
            </CardHeader>
            <CardContent>
              <CardDescription>
                Centralized management of partner information
              </CardDescription>
            </CardContent>
          </Card>

          <Card className="border-border hover:shadow-xl transition-all duration-300 hover:-translate-y-1">
            <CardHeader className="flex flex-row items-center gap-4">
              <div className="p-3 rounded-lg bg-orange-100">
                <Settings className="w-6 h-6 text-orange-700" />
              </div>
              <CardTitle className="text-lg">System Configuration</CardTitle>
            </CardHeader>
            <CardContent>
              <CardDescription>
                Customize workflows, access rights, and notifications
              </CardDescription>
            </CardContent>
          </Card>
        </div>

        {/* Smaller status cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          <Card className="hover:shadow-lg transition-shadow">
            <CardHeader className="flex items-center gap-3">
              <Package className="w-5 h-5 text-blue-600" />
              <CardTitle className="text-lg">Warehouse Operations</CardTitle>
            </CardHeader>
            <CardContent>
              <CardDescription className="text-base">
                The warehouse system is running normally and ready for processing
              </CardDescription>
            </CardContent>
          </Card>

          <Card className="hover:shadow-lg transition-shadow">
            <CardHeader className="flex items-center gap-3">
              <Truck className="w-5 h-5 text-green-600" />
              <CardTitle className="text-lg">Shipping & Receiving</CardTitle>
            </CardHeader>
            <CardContent>
              <CardDescription className="text-base">
                Delivery and receiving processes are running smoothly
              </CardDescription>
            </CardContent>
          </Card>

          <Card className="hover:shadow-lg transition-shadow">
            <CardHeader className="flex items-center gap-3">
              <CheckCircle className="w-5 h-5 text-emerald-600" />
              <CardTitle className="text-lg">System Stability</CardTitle>
            </CardHeader>
            <CardContent>
              <CardDescription className="text-base">
                All services are operating well and securely
              </CardDescription>
            </CardContent>
          </Card>
        </div>

        {/* Closing message */}
        <div className="text-center py-12">
          <p className="text-2xl font-medium text-foreground">
            Everything is ready!
          </p>
          <p className="text-lg text-muted-foreground mt-4">
            You can start using the system anytime.
          </p>
          <p className="text-sm text-muted-foreground mt-8 italic">
            (This is a demo dashboard page – just here to look beautiful 🌟)
          </p>
        </div>
      </div>
    </div>
  )
}