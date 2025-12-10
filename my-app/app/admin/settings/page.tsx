"use client"

// import { Header } from "@/components/header"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Label } from "@/components/ui/label"
import { SettingsIcon, Users, Warehouse, Bell } from "lucide-react"
import { Header } from "@/components/custom/header"

export default function AdminSettingsPage() {
  return (
    <div className="w-full">
      <Header title="Settings" subtitle="Configure system-wide settings and policies" />

      <div className="p-6 space-y-6">
        <Tabs defaultValue="general" className="w-full">
          <TabsList className="grid w-full grid-cols-4 bg-sidebar border-border">
            <TabsTrigger value="general" className="data-[state=active]:bg-primary">
              <SettingsIcon size={16} className="mr-2" />
              General
            </TabsTrigger>
            <TabsTrigger value="vendors" className="data-[state=active]:bg-primary">
              <Users size={16} className="mr-2" />
              Vendors
            </TabsTrigger>
            <TabsTrigger value="warehouses" className="data-[state=active]:bg-primary">
              <Warehouse size={16} className="mr-2" />
              Warehouses
            </TabsTrigger>
            <TabsTrigger value="notifications" className="data-[state=active]:bg-primary">
              <Bell size={16} className="mr-2" />
              Notifications
            </TabsTrigger>
          </TabsList>

          {/* General Tab */}
          <TabsContent value="general" className="space-y-6 mt-6">
            <Card className="bg-card border-border">
              <CardHeader>
                <CardTitle>Platform Configuration</CardTitle>
                <CardDescription>Configure system-wide settings</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="platform-name">Platform Name</Label>
                  <Input id="platform-name" defaultValue="Warehouse Hub" className="bg-sidebar border-border" />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="support-email">Support Email</Label>
                  <Input
                    id="support-email"
                    type="email"
                    defaultValue="support@warehousehub.com"
                    className="bg-sidebar border-border"
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="commission">Commission Rate (%)</Label>
                  <Input id="commission" type="number" defaultValue="2.5" className="bg-sidebar border-border" />
                </div>

                <Button className="bg-primary text-primary-foreground hover:bg-primary/90">Save Settings</Button>
              </CardContent>
            </Card>
          </TabsContent>

          {/* Vendors Tab */}
          <TabsContent value="vendors" className="space-y-6 mt-6">
            <Card className="bg-card border-border">
              <CardHeader>
                <CardTitle>Vendor Policies</CardTitle>
                <CardDescription>Configure vendor-related settings</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="min-products">Minimum Products Required</Label>
                  <Input id="min-products" type="number" defaultValue="5" className="bg-sidebar border-border" />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="approval-time">Vendor Approval Time (days)</Label>
                  <Input id="approval-time" type="number" defaultValue="3" className="bg-sidebar border-border" />
                </div>

                <div className="space-y-2">
                  <Label className="flex items-center gap-2">
                    <input type="checkbox" defaultChecked className="w-4 h-4" />
                    Auto-approve vendors with complete documents
                  </Label>
                </div>

                <Button className="bg-primary text-primary-foreground hover:bg-primary/90">Save Policy</Button>
              </CardContent>
            </Card>
          </TabsContent>

          {/* Warehouses Tab */}
          <TabsContent value="warehouses" className="space-y-6 mt-6">
            <Card className="bg-card border-border">
              <CardHeader>
                <CardTitle>Warehouse Configuration</CardTitle>
                <CardDescription>Manage warehouse capacity and settings</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="default-capacity">Default Warehouse Capacity</Label>
                  <Input
                    id="default-capacity"
                    type="number"
                    defaultValue="50000"
                    className="bg-sidebar border-border"
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="alert-threshold">Low Stock Alert Threshold (%)</Label>
                  <Input id="alert-threshold" type="number" defaultValue="20" className="bg-sidebar border-border" />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="safety-stock">Default Safety Stock Level</Label>
                  <Input id="safety-stock" type="number" defaultValue="10" className="bg-sidebar border-border" />
                </div>

                <Button className="bg-primary text-primary-foreground hover:bg-primary/90">Save Configuration</Button>
              </CardContent>
            </Card>
          </TabsContent>

          {/* Notifications Tab */}
          <TabsContent value="notifications" className="space-y-6 mt-6">
            <Card className="bg-card border-border">
              <CardHeader>
                <CardTitle>Notification Settings</CardTitle>
                <CardDescription>Configure system notifications</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                {[
                  { label: "Vendor Registration", desc: "Send alerts when new vendors register" },
                  { label: "Low Inventory", desc: "Alert about low stock levels" },
                  { label: "Order Issues", desc: "Notify about order problems" },
                  { label: "System Alerts", desc: "Critical system notifications" },
                ].map((notif, idx) => (
                  <div key={idx} className="flex items-center justify-between p-3 bg-sidebar rounded-lg">
                    <div>
                      <p className="text-sm font-medium text-foreground">{notif.label}</p>
                      <p className="text-xs text-muted-foreground">{notif.desc}</p>
                    </div>
                    <input type="checkbox" defaultChecked className="w-4 h-4" />
                  </div>
                ))}

                <Button className="bg-primary text-primary-foreground hover:bg-primary/90">Save Preferences</Button>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  )
}
