"use client"

import { useState } from "react"
// import { Header } from "@/components/header"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Search, Plus, MapPin } from "lucide-react"
import { Header } from "@/components/custom/header"

interface Warehouse {
  id: string
  name: string
  code: string
  location: string
  region: string
  status: "ACTIVE" | "MAINTENANCE"
  totalProducts: number
  capacity: number
  occupancy: number
  managers: number
}

const warehouses: Warehouse[] = [
  {
    id: "1",
    name: "Central Distribution Hub",
    code: "WH-001",
    location: "Ho Chi Minh City",
    region: "South",
    status: "ACTIVE",
    totalProducts: 15420,
    capacity: 50000,
    occupancy: 31,
    managers: 3,
  },
  {
    id: "2",
    name: "Northern Logistics Center",
    code: "WH-002",
    location: "Hanoi",
    region: "North",
    status: "ACTIVE",
    totalProducts: 12350,
    capacity: 40000,
    occupancy: 31,
    managers: 2,
  },
  {
    id: "3",
    name: "Regional Storage Facility",
    code: "WH-003",
    location: "Da Nang",
    region: "Central",
    status: "ACTIVE",
    totalProducts: 8900,
    capacity: 30000,
    occupancy: 30,
    managers: 2,
  },
  {
    id: "4",
    name: "Express Fulfillment Center",
    code: "WH-004",
    location: "Binh Duong",
    region: "South",
    status: "MAINTENANCE",
    totalProducts: 5230,
    capacity: 25000,
    occupancy: 21,
    managers: 1,
  },
  {
    id: "5",
    name: "Secondary Distribution Point",
    code: "WH-005",
    location: "Can Tho",
    region: "Mekong",
    status: "ACTIVE",
    totalProducts: 6780,
    capacity: 20000,
    occupancy: 34,
    managers: 1,
  },
]

const getOccupancyColor = (occupancy: number) => {
  if (occupancy > 80) return "text-red-500"
  if (occupancy > 60) return "text-yellow-500"
  return "text-green-500"
}

export default function WarehousesPage() {
  const [searchTerm, setSearchTerm] = useState("")
  const [filteredWarehouses, setFilteredWarehouses] = useState(warehouses)

  const handleSearch = (term: string) => {
    setSearchTerm(term)
    const filtered = warehouses.filter(
      (wh) =>
        wh.name.toLowerCase().includes(term.toLowerCase()) ||
        wh.code.toLowerCase().includes(term.toLowerCase()) ||
        wh.location.toLowerCase().includes(term.toLowerCase()),
    )
    setFilteredWarehouses(filtered)
  }

  const totalCapacity = warehouses.reduce((sum, w) => sum + w.capacity, 0)
  const totalProducts = warehouses.reduce((sum, w) => sum + w.totalProducts, 0)
  const averageOccupancy = Math.round(warehouses.reduce((sum, w) => sum + w.occupancy, 0) / warehouses.length)

  return (
    <div className="w-full">
      <Header title="Warehouses" subtitle="Manage warehouse locations and inventory distribution" />

      <div className="p-6 space-y-6">
        {/* Stats */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <Card className="bg-card border-border">
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Total Warehouses</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-foreground">{warehouses.length}</div>
              <p className="text-xs text-accent mt-1">Across 4 regions</p>
            </CardContent>
          </Card>

          <Card className="bg-card border-border">
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Total Capacity</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-foreground">{(totalCapacity / 1000).toFixed(0)}K</div>
              <p className="text-xs text-muted-foreground mt-1">Storage units</p>
            </CardContent>
          </Card>

          <Card className="bg-card border-border">
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Total Products</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-accent">{(totalProducts / 1000).toFixed(0)}K</div>
              <p className="text-xs text-muted-foreground mt-1">In inventory</p>
            </CardContent>
          </Card>

          <Card className="bg-card border-border">
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Avg Occupancy</CardTitle>
            </CardHeader>
            <CardContent>
              <div className={`text-2xl font-bold ${getOccupancyColor(averageOccupancy)}`}>{averageOccupancy}%</div>
              <p className="text-xs text-muted-foreground mt-1">Capacity utilization</p>
            </CardContent>
          </Card>
        </div>

        {/* Search and Add */}
        <div className="flex flex-col md:flex-row gap-4 items-start md:items-center justify-between">
          <div className="relative w-full md:w-64">
            <Search className="absolute left-3 top-3 w-4 h-4 text-muted-foreground" />
            <Input
              placeholder="Search warehouses..."
              className="pl-10 bg-card border-border"
              value={searchTerm}
              onChange={(e) => handleSearch(e.target.value)}
            />
          </div>
          <Button className="bg-primary text-primary-foreground hover:bg-primary/90">
            <Plus size={18} className="mr-2" />
            Add Warehouse
          </Button>
        </div>

        {/* Warehouses Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {filteredWarehouses.map((warehouse) => (
            <Card key={warehouse.id} className="bg-card border-border hover:border-primary/50 transition-colors">
              <CardHeader>
                <div className="flex items-start justify-between">
                  <div>
                    <CardTitle className="text-lg">{warehouse.name}</CardTitle>
                    <CardDescription className="text-xs mt-1">{warehouse.code}</CardDescription>
                  </div>
                  <Badge
                    variant="outline"
                    className={
                      warehouse.status === "ACTIVE"
                        ? "bg-green-500/10 text-green-500 border-0"
                        : "bg-yellow-500/10 text-yellow-500 border-0"
                    }
                  >
                    {warehouse.status}
                  </Badge>
                </div>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="flex items-center gap-2 text-sm">
                  <MapPin size={16} className="text-primary" />
                  <div>
                    <p className="text-foreground font-medium">{warehouse.location}</p>
                    <p className="text-muted-foreground text-xs">Region: {warehouse.region}</p>
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-3">
                  <div className="bg-sidebar/50 p-3 rounded-lg">
                    <p className="text-xs text-muted-foreground mb-1">Products</p>
                    <p className="text-lg font-bold text-accent">{warehouse.totalProducts.toLocaleString()}</p>
                  </div>
                  <div className="bg-sidebar/50 p-3 rounded-lg">
                    <p className="text-xs text-muted-foreground mb-1">Managers</p>
                    <p className="text-lg font-bold text-primary">{warehouse.managers}</p>
                  </div>
                </div>

                <div>
                  <div className="flex justify-between items-center mb-2">
                    <p className="text-xs font-medium text-muted-foreground">Occupancy</p>
                    <p className={`text-sm font-bold ${getOccupancyColor(warehouse.occupancy)}`}>
                      {warehouse.occupancy}%
                    </p>
                  </div>
                  <div className="w-full bg-sidebar rounded-full h-2 overflow-hidden">
                    <div
                      className={`h-full transition-all ${
                        warehouse.occupancy > 80
                          ? "bg-red-500"
                          : warehouse.occupancy > 60
                            ? "bg-yellow-500"
                            : "bg-green-500"
                      }`}
                      style={{ width: `${warehouse.occupancy}%` }}
                    />
                  </div>
                </div>

                <Button className="w-full bg-primary/20 text-primary hover:bg-primary/30" variant="outline">
                  View Details
                </Button>
              </CardContent>
            </Card>
          ))}
        </div>
      </div>
    </div>
  )
}
