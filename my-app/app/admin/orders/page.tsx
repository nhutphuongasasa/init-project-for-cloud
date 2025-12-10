"use client"

import { useState } from "react"
// import { Header } from "@/components/header"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Search, Plus, MoreHorizontal } from "lucide-react"
import { Header } from "@/components/custom/header"

interface Order {
  id: string
  orderCode: string
  vendor: string
  warehouse: string
  customer: string
  items: number
  status: "PENDING" | "PICKING" | "PACKED" | "SHIPPED"
  total: string
  createdAt: string
}

const orders: Order[] = [
  {
    id: "1",
    orderCode: "ORD-20250308-001",
    vendor: "TechStore Vietnam",
    warehouse: "Ho Chi Minh City",
    customer: "John Doe",
    items: 3,
    status: "SHIPPED",
    total: "$245.99",
    createdAt: "Mar 08, 2025",
  },
  {
    id: "2",
    orderCode: "ORD-20250308-002",
    vendor: "Fashion Hub Co.",
    warehouse: "Hanoi",
    customer: "Jane Smith",
    items: 1,
    status: "PACKED",
    total: "$89.99",
    createdAt: "Mar 08, 2025",
  },
  {
    id: "3",
    orderCode: "ORD-20250308-003",
    vendor: "Electronics Plus",
    warehouse: "Da Nang",
    customer: "Mike Johnson",
    items: 2,
    status: "PICKING",
    total: "$599.98",
    createdAt: "Mar 08, 2025",
  },
  {
    id: "4",
    orderCode: "ORD-20250308-004",
    vendor: "Home Essentials",
    warehouse: "Ho Chi Minh City",
    customer: "Sarah Wilson",
    items: 5,
    status: "PENDING",
    total: "$125.50",
    createdAt: "Mar 08, 2025",
  },
]

const statusConfig = {
  PENDING: { bg: "bg-blue-500/10", text: "text-blue-500" },
  PICKING: { bg: "bg-yellow-500/10", text: "text-yellow-500" },
  PACKED: { bg: "bg-purple-500/10", text: "text-purple-500" },
  SHIPPED: { bg: "bg-green-500/10", text: "text-green-500" },
}

export default function AdminOrdersPage() {
  const [searchTerm, setSearchTerm] = useState("")
  const [filteredOrders, setFilteredOrders] = useState(orders)

  const handleSearch = (term: string) => {
    setSearchTerm(term)
    const filtered = orders.filter(
      (order) =>
        order.orderCode.toLowerCase().includes(term.toLowerCase()) ||
        order.customer.toLowerCase().includes(term.toLowerCase()),
    )
    setFilteredOrders(filtered)
  }

  const totalOrders = orders.length
  const pendingOrders = orders.filter((o) => o.status === "PENDING").length
  const shippedOrders = orders.filter((o) => o.status === "SHIPPED").length

  return (
    <div className="w-full">
      <Header title="Orders" subtitle="Monitor all fulfillment operations" />

      <div className="p-6 space-y-6">
        {/* Stats */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <Card className="bg-card border-border">
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Total Orders</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-foreground">{totalOrders}</div>
            </CardContent>
          </Card>

          <Card className="bg-card border-border">
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Pending</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-blue-500">{pendingOrders}</div>
            </CardContent>
          </Card>

          <Card className="bg-card border-border">
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">In Transit</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-yellow-500">2</div>
            </CardContent>
          </Card>

          <Card className="bg-card border-border">
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Shipped</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-green-500">{shippedOrders}</div>
            </CardContent>
          </Card>
        </div>

        {/* Search and Add */}
        <div className="flex flex-col md:flex-row gap-4 items-start md:items-center justify-between">
          <div className="relative w-full md:w-64">
            <Search className="absolute left-3 top-3 w-4 h-4 text-muted-foreground" />
            <Input
              placeholder="Search orders..."
              className="pl-10 bg-card border-border"
              value={searchTerm}
              onChange={(e) => handleSearch(e.target.value)}
            />
          </div>
          <Button className="bg-primary text-primary-foreground hover:bg-primary/90">
            <Plus size={18} className="mr-2" />
            Create Order
          </Button>
        </div>

        {/* Orders Table */}
        <Card className="bg-card border-border">
          <CardHeader>
            <CardTitle>Orders ({filteredOrders.length})</CardTitle>
            <CardDescription>Track and manage all customer orders</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-border">
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Order Code</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Vendor</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Customer</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Warehouse</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Items</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Status</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Total</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Created</th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredOrders.map((order) => (
                    <tr key={order.id} className="border-b border-border hover:bg-sidebar/30 transition-colors">
                      <td className="py-4 px-4 text-sm font-mono font-medium text-primary">{order.orderCode}</td>
                      <td className="py-4 px-4 text-sm text-foreground">{order.vendor}</td>
                      <td className="py-4 px-4 text-sm text-muted-foreground">{order.customer}</td>
                      <td className="py-4 px-4 text-sm text-muted-foreground">{order.warehouse}</td>
                      <td className="py-4 px-4 text-sm text-foreground">{order.items}</td>
                      <td className="py-4 px-4 text-sm">
                        <Badge
                          variant="outline"
                          className={`${statusConfig[order.status].bg} ${statusConfig[order.status].text} border-0`}
                        >
                          {order.status}
                        </Badge>
                      </td>
                      <td className="py-4 px-4 text-sm font-semibold text-accent">{order.total}</td>
                      <td className="py-4 px-4 text-sm text-muted-foreground">{order.createdAt}</td>
                      <td className="py-4 px-4 text-sm">
                        <Button variant="ghost" size="icon">
                          <MoreHorizontal size={16} />
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
