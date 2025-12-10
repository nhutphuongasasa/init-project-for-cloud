"use client"

import { useState } from "react"
// import { Header } from "@/components/header"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Search, Plus, MoreHorizontal, AlertTriangle } from "lucide-react"
import { Header } from "@/components/custom/header"

interface InventoryItem {
  id: string
  sku: string
  productName: string
  warehouse: string
  available: number
  reserved: number
  safetyStock: number
  status: "OPTIMAL" | "LOW" | "CRITICAL"
}

const inventoryItems: InventoryItem[] = [
  {
    id: "1",
    sku: "LP-001",
    productName: "Premium Laptop Backpack",
    warehouse: "Ho Chi Minh City",
    available: 245,
    reserved: 32,
    safetyStock: 50,
    status: "OPTIMAL",
  },
  {
    id: "2",
    sku: "HUB-C-002",
    productName: "USB-C Hub Pro",
    warehouse: "Hanoi",
    available: 512,
    reserved: 89,
    safetyStock: 100,
    status: "OPTIMAL",
  },
  {
    id: "3",
    sku: "KB-MEC-001",
    productName: "Mechanical Keyboard",
    warehouse: "Da Nang",
    available: 12,
    reserved: 5,
    safetyStock: 50,
    status: "CRITICAL",
  },
  {
    id: "4",
    sku: "MOUSE-W-001",
    productName: "Wireless Mouse",
    warehouse: "Ho Chi Minh City",
    available: 890,
    reserved: 156,
    safetyStock: 100,
    status: "OPTIMAL",
  },
  {
    id: "5",
    sku: "KEY-W-002",
    productName: "Wireless Keyboard",
    warehouse: "Binh Duong",
    available: 45,
    reserved: 12,
    safetyStock: 50,
    status: "LOW",
  },
]

const statusConfig = {
  OPTIMAL: { bg: "bg-green-500/10", text: "text-green-500", icon: null },
  LOW: { bg: "bg-yellow-500/10", text: "text-yellow-500", icon: AlertTriangle },
  CRITICAL: { bg: "bg-red-500/10", text: "text-red-500", icon: AlertTriangle },
}

export default function VendorInventoryPage() {
  const [searchTerm, setSearchTerm] = useState("")
  const [filteredItems, setFilteredItems] = useState(inventoryItems)

  const handleSearch = (term: string) => {
    setSearchTerm(term)
    const filtered = inventoryItems.filter(
      (item) =>
        item.productName.toLowerCase().includes(term.toLowerCase()) ||
        item.sku.toLowerCase().includes(term.toLowerCase()),
    )
    setFilteredItems(filtered)
  }

  const totalStock = inventoryItems.reduce((sum, item) => sum + item.available, 0)
  const lowStockItems = inventoryItems.filter((item) => item.available < item.safetyStock).length

  return (
    <div className="w-full">
      <Header title="Inventory" subtitle="Track and manage your stock across warehouses" />

      <div className="p-6 space-y-6">
        {/* Stats */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <Card className="bg-card border-border">
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Total Stock</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-accent">{totalStock.toLocaleString()}</div>
              <p className="text-xs text-muted-foreground mt-1">Units available</p>
            </CardContent>
          </Card>

          <Card className="bg-card border-border">
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Low Stock Items</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-yellow-500">{lowStockItems}</div>
              <p className="text-xs text-muted-foreground mt-1">Below safety threshold</p>
            </CardContent>
          </Card>

          <Card className="bg-card border-border">
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Reserved Stock</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-primary">
                {inventoryItems.reduce((sum, item) => sum + item.reserved, 0)}
              </div>
              <p className="text-xs text-muted-foreground mt-1">For pending orders</p>
            </CardContent>
          </Card>
        </div>

        {/* Search and Add */}
        <div className="flex flex-col md:flex-row gap-4 items-start md:items-center justify-between">
          <div className="relative w-full md:w-64">
            <Search className="absolute left-3 top-3 w-4 h-4 text-muted-foreground" />
            <Input
              placeholder="Search inventory..."
              className="pl-10 bg-card border-border"
              value={searchTerm}
              onChange={(e) => handleSearch(e.target.value)}
            />
          </div>
          <Button className="bg-primary text-primary-foreground hover:bg-primary/90">
            <Plus size={18} className="mr-2" />
            Add Stock
          </Button>
        </div>

        {/* Inventory Table */}
        <Card className="bg-card border-border">
          <CardHeader>
            <CardTitle>Inventory Details ({filteredItems.length})</CardTitle>
            <CardDescription>Real-time stock levels and movement</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-border">
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Product</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">SKU</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Warehouse</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Available</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Reserved</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Safety Stock</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Status</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredItems.map((item) => {
                    const StatusIcon = statusConfig[item.status].icon
                    return (
                      <tr key={item.id} className="border-b border-border hover:bg-sidebar/30 transition-colors">
                        <td className="py-4 px-4 text-sm font-medium text-foreground">{item.productName}</td>
                        <td className="py-4 px-4 text-sm text-muted-foreground font-mono">{item.sku}</td>
                        <td className="py-4 px-4 text-sm text-muted-foreground">{item.warehouse}</td>
                        <td className="py-4 px-4 text-sm font-semibold text-accent">{item.available}</td>
                        <td className="py-4 px-4 text-sm text-primary">{item.reserved}</td>
                        <td className="py-4 px-4 text-sm text-muted-foreground">{item.safetyStock}</td>
                        <td className="py-4 px-4 text-sm">
                          <Badge
                            variant="outline"
                            className={`${statusConfig[item.status].bg} ${statusConfig[item.status].text} border-0`}
                          >
                            {StatusIcon && <StatusIcon size={14} className="mr-1" />}
                            {item.status}
                          </Badge>
                        </td>
                        <td className="py-4 px-4 text-sm">
                          <Button variant="ghost" size="icon">
                            <MoreHorizontal size={16} />
                          </Button>
                        </td>
                      </tr>
                    )
                  })}
                </tbody>
              </table>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
