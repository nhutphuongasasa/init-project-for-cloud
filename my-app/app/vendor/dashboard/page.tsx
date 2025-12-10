"use client"

// import { Header } from "@/components/header"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import {
  BarChart,
  Bar,
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts"
import { TrendingUp, Package, ShoppingCart, Warehouse, AlertTriangle } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Header } from "@/components/custom/header"

const salesData = [
  { name: "Mon", sales: 2400, orders: 24 },
  { name: "Tue", sales: 1398, orders: 22 },
  { name: "Wed", sales: 9800, orders: 29 },
  { name: "Thu", sales: 3908, orders: 20 },
  { name: "Fri", sales: 4800, orders: 32 },
  { name: "Sat", sales: 3800, orders: 23 },
  { name: "Sun", sales: 4300, orders: 27 },
]

const inventoryTrend = [
  { name: "Week 1", inbound: 2400, outbound: 2400 },
  { name: "Week 2", inbound: 3000, outbound: 1398 },
  { name: "Week 3", inbound: 2000, outbound: 9800 },
  { name: "Week 4", inbound: 2780, outbound: 3908 },
]

const recentActivities = [
  {
    id: 1,
    type: "order",
    title: "New Order Received",
    description: "Order OUT-045 placed by customer",
    time: "2 hours ago",
  },
  {
    id: 2,
    type: "inventory",
    title: "Low Stock Alert",
    description: "Product KB-MEC-001 below safety level",
    time: "4 hours ago",
  },
  {
    id: 3,
    type: "order",
    title: "Order Shipped",
    description: "Order OUT-044 shipped successfully",
    time: "6 hours ago",
  },
  {
    id: 4,
    type: "inventory",
    title: "Stock Received",
    description: "50 units of LP-001 received",
    time: "1 day ago",
  },
]

export default function VendorDashboard() {
  return (
    <div className="w-full">
      <Header title="Dashboard" subtitle="Welcome to your seller portal" />

      <div className="p-6 space-y-6">
        {/* KPI Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <Card className="bg-card border-border">
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Total Revenue</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex items-center justify-between">
                <div>
                  <div className="text-3xl font-bold text-foreground">$24,520</div>
                  <p className="text-xs text-accent mt-1 flex items-center gap-1">
                    <TrendingUp size={14} /> +12% this month
                  </p>
                </div>
                <div className="text-accent opacity-20">
                  <TrendingUp size={32} />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card className="bg-card border-border">
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Active Products</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex items-center justify-between">
                <div>
                  <div className="text-3xl font-bold text-foreground">47</div>
                  <p className="text-xs text-primary mt-1">In catalog</p>
                </div>
                <div className="text-primary opacity-20">
                  <Package size={32} />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card className="bg-card border-border">
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Pending Orders</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex items-center justify-between">
                <div>
                  <div className="text-3xl font-bold text-foreground">8</div>
                  <p className="text-xs text-yellow-500 mt-1">Need processing</p>
                </div>
                <div className="text-yellow-500 opacity-20">
                  <ShoppingCart size={32} />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card className="bg-card border-border">
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Total Stock</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex items-center justify-between">
                <div>
                  <div className="text-3xl font-bold text-foreground">1,847</div>
                  <p className="text-xs text-muted-foreground mt-1">Units</p>
                </div>
                <div className="text-secondary opacity-20">
                  <Warehouse size={32} />
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Charts */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Sales & Orders Chart */}
          <Card className="bg-card border-border">
            <CardHeader>
              <CardTitle>Sales Overview</CardTitle>
              <CardDescription>Last 7 days sales performance</CardDescription>
            </CardHeader>
            <CardContent>
              <ResponsiveContainer width="100%" height={300}>
                <AreaChart data={salesData}>
                  <defs>
                    <linearGradient id="colorSales" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="var(--color-accent)" stopOpacity={0.3} />
                      <stop offset="95%" stopColor="var(--color-accent)" stopOpacity={0} />
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" stroke="var(--color-border)" />
                  <XAxis stroke="var(--color-muted-foreground)" />
                  <YAxis stroke="var(--color-muted-foreground)" />
                  <Tooltip
                    contentStyle={{
                      backgroundColor: "var(--color-card)",
                      border: "1px solid var(--color-border)",
                      borderRadius: "8px",
                    }}
                  />
                  <Area
                    type="monotone"
                    dataKey="sales"
                    stroke="var(--color-accent)"
                    fillOpacity={1}
                    fill="url(#colorSales)"
                  />
                </AreaChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>

          {/* Inventory Movement */}
          <Card className="bg-card border-border">
            <CardHeader>
              <CardTitle>Inventory Movement</CardTitle>
              <CardDescription>Inbound vs Outbound by week</CardDescription>
            </CardHeader>
            <CardContent>
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={inventoryTrend}>
                  <CartesianGrid strokeDasharray="3 3" stroke="var(--color-border)" />
                  <XAxis stroke="var(--color-muted-foreground)" />
                  <YAxis stroke="var(--color-muted-foreground)" />
                  <Tooltip
                    contentStyle={{
                      backgroundColor: "var(--color-card)",
                      border: "1px solid var(--color-border)",
                      borderRadius: "8px",
                    }}
                  />
                  <Legend />
                  <Bar dataKey="inbound" fill="var(--color-primary)" />
                  <Bar dataKey="outbound" fill="var(--color-accent)" />
                </BarChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </div>

        {/* Alerts and Activities */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Alerts */}
          <Card className="bg-card border-border">
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <AlertTriangle size={20} className="text-yellow-500" />
                Alerts & Warnings
              </CardTitle>
              <CardDescription>Issues requiring attention</CardDescription>
            </CardHeader>
            <CardContent className="space-y-3">
              <div className="flex gap-3 p-3 bg-yellow-500/10 rounded-lg border border-yellow-500/20">
                <AlertTriangle size={18} className="text-yellow-500 flex-shrink-0 mt-0.5" />
                <div>
                  <p className="text-sm font-medium text-foreground">Low Stock Alert</p>
                  <p className="text-xs text-muted-foreground">3 products below safety level</p>
                </div>
              </div>

              <div className="flex gap-3 p-3 bg-red-500/10 rounded-lg border border-red-500/20">
                <AlertTriangle size={18} className="text-red-500 flex-shrink-0 mt-0.5" />
                <div>
                  <p className="text-sm font-medium text-foreground">Pending Order Review</p>
                  <p className="text-xs text-muted-foreground">2 orders waiting for confirmation</p>
                </div>
              </div>

              <Button variant="outline" className="w-full text-sm bg-transparent">
                View All Alerts
              </Button>
            </CardContent>
          </Card>

          {/* Recent Activity */}
          <Card className="bg-card border-border">
            <CardHeader>
              <CardTitle>Recent Activity</CardTitle>
              <CardDescription>Latest updates from your store</CardDescription>
            </CardHeader>
            <CardContent className="space-y-3">
              {recentActivities.map((activity) => (
                <div key={activity.id} className="flex gap-3 pb-3 border-b border-border last:border-0">
                  <div
                    className={`w-2 h-2 rounded-full mt-2 flex-shrink-0 ${
                      activity.type === "order" ? "bg-blue-500" : "bg-yellow-500"
                    }`}
                  />
                  <div className="flex-1">
                    <p className="text-sm font-medium text-foreground">{activity.title}</p>
                    <p className="text-xs text-muted-foreground">{activity.description}</p>
                    <p className="text-xs text-muted-foreground mt-1">{activity.time}</p>
                  </div>
                </div>
              ))}
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  )
}
