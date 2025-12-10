"use client"

import { useState } from "react"
// import { Header } from "@/components/header"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Search, Plus, MoreHorizontal, ArrowDown, ArrowUp } from "lucide-react"
import { Header } from "@/components/custom/header"

interface OrderData {
  id: string
  code: string
  type: "OUTBOUND" | "INBOUND"
  warehouse: string
  items: number
  status: "DRAFT" | "CONFIRMED" | "COMPLETED" | "CANCELLED"
  date: string
  total?: string
}

const outboundOrders: OrderData[] = [
  {
    id: "1",
    code: "OUT-001",
    type: "OUTBOUND",
    warehouse: "Ho Chi Minh City",
    items: 3,
    status: "COMPLETED",
    date: "Mar 08, 2025",
    total: "$245.99",
  },
  {
    id: "2",
    code: "OUT-002",
    type: "OUTBOUND",
    warehouse: "Hanoi",
    items: 1,
    status: "CONFIRMED",
    date: "Mar 07, 2025",
    total: "$89.99",
  },
  {
    id: "3",
    code: "OUT-003",
    type: "OUTBOUND",
    warehouse: "Da Nang",
    items: 2,
    status: "DRAFT",
    date: "Mar 07, 2025",
    total: "$599.98",
  },
]

const inboundOrders: OrderData[] = [
  {
    id: "1",
    code: "IN-001",
    type: "INBOUND",
    warehouse: "Ho Chi Minh City",
    items: 50,
    status: "COMPLETED",
    date: "Mar 06, 2025",
  },
  {
    id: "2",
    code: "IN-002",
    type: "INBOUND",
    warehouse: "Hanoi",
    items: 30,
    status: "CONFIRMED",
    date: "Mar 07, 2025",
  },
  {
    id: "3",
    code: "IN-003",
    type: "INBOUND",
    warehouse: "Da Nang",
    items: 100,
    status: "DRAFT",
    date: "Mar 08, 2025",
  },
]

const statusConfig = {
  DRAFT: { bg: "bg-gray-500/10", text: "text-gray-500" },
  CONFIRMED: { bg: "bg-blue-500/10", text: "text-blue-500" },
  COMPLETED: { bg: "bg-green-500/10", text: "text-green-500" },
  CANCELLED: { bg: "bg-red-500/10", text: "text-red-500" },
}

function OrderTable({ orders, type }: { orders: OrderData[]; type: "OUTBOUND" | "INBOUND" }) {
  const [searchTerm, setSearchTerm] = useState("")
  const filtered = orders.filter((o) => o.code.toLowerCase().includes(searchTerm.toLowerCase()))

  return (
    <div className="space-y-4">
      <div className="relative w-full md:w-64">
        <Search className="absolute left-3 top-3 w-4 h-4 text-muted-foreground" />
        <Input
          placeholder={`Search ${type} orders...`}
          className="pl-10 bg-card border-border"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
      </div>

      <div className="overflow-x-auto">
        <table className="w-full">
          <thead>
            <tr className="border-b border-border">
              <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Order Code</th>
              <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Warehouse</th>
              <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Items</th>
              <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Status</th>
              <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Date</th>
              {type === "OUTBOUND" && (
                <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Total</th>
              )}
              <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Actions</th>
            </tr>
          </thead>
          <tbody>
            {filtered.map((order) => (
              <tr key={order.id} className="border-b border-border hover:bg-sidebar/30 transition-colors">
                <td className="py-4 px-4 text-sm font-mono font-medium text-primary">{order.code}</td>
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
                <td className="py-4 px-4 text-sm text-muted-foreground">{order.date}</td>
                {type === "OUTBOUND" && <td className="py-4 px-4 text-sm font-semibold text-accent">{order.total}</td>}
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
    </div>
  )
}

export default function VendorOrdersPage() {
  const outboundCount = outboundOrders.length
  const inboundCount = inboundOrders.length

  return (
    <div className="w-full">
      <Header title="Orders" subtitle="Manage fulfillment and inbound orders" />

      <div className="p-6 space-y-6">
        {/* Stats */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <Card className="bg-card border-border">
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Outbound Orders</CardTitle>
            </CardHeader>
            <CardContent className="flex items-center justify-between">
              <div className="text-2xl font-bold text-foreground">{outboundCount}</div>
              <ArrowUp size={32} className="text-accent opacity-20" />
            </CardContent>
          </Card>

          <Card className="bg-card border-border">
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Inbound Orders</CardTitle>
            </CardHeader>
            <CardContent className="flex items-center justify-between">
              <div className="text-2xl font-bold text-foreground">{inboundCount}</div>
              <ArrowDown size={32} className="text-primary opacity-20" />
            </CardContent>
          </Card>

          <Card className="bg-card border-border">
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Pending Actions</CardTitle>
            </CardHeader>
            <CardContent className="flex items-center justify-between">
              <div className="text-2xl font-bold text-yellow-500">
                {outboundOrders.filter((o) => o.status === "DRAFT").length +
                  inboundOrders.filter((o) => o.status === "DRAFT").length}
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Orders Tabs */}
        <Card className="bg-card border-border">
          <CardHeader>
            <CardTitle>Order Management</CardTitle>
            <CardDescription>Track your inbound and outbound orders</CardDescription>
          </CardHeader>
          <CardContent>
            <Tabs defaultValue="outbound" className="w-full">
              <TabsList className="bg-sidebar border-border">
                <TabsTrigger value="outbound" className="data-[state=active]:bg-primary">
                  Outbound Orders
                </TabsTrigger>
                <TabsTrigger value="inbound" className="data-[state=active]:bg-primary">
                  Inbound Orders
                </TabsTrigger>
              </TabsList>

              <TabsContent value="outbound" className="mt-6 space-y-4">
                <div className="flex justify-end">
                  <Button className="bg-primary text-primary-foreground hover:bg-primary/90">
                    <Plus size={18} className="mr-2" />
                    Create Outbound
                  </Button>
                </div>
                <OrderTable orders={outboundOrders} type="OUTBOUND" />
              </TabsContent>

              <TabsContent value="inbound" className="mt-6 space-y-4">
                <div className="flex justify-end">
                  <Button className="bg-primary text-primary-foreground hover:bg-primary/90">
                    <Plus size={18} className="mr-2" />
                    Create Inbound
                  </Button>
                </div>
                <OrderTable orders={inboundOrders} type="INBOUND" />
              </TabsContent>
            </Tabs>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
