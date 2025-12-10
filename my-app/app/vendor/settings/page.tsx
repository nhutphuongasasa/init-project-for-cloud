"use client"

// import { Header } from "@/components/header"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Label } from "@/components/ui/label"
import { Bell, Lock, User, FileText } from "lucide-react"
import { Header } from "@/components/custom/header"

export default function VendorSettingsPage() {
  return (
    <div className="w-full">
      <Header title="Settings" subtitle="Manage your store settings and preferences" />

      <div className="p-6 space-y-6">
        <Tabs defaultValue="profile" className="w-full">
          <TabsList className="grid w-full grid-cols-4 bg-sidebar border-border">
            <TabsTrigger value="profile" className="data-[state=active]:bg-primary">
              <User size={16} className="mr-2" />
              Profile
            </TabsTrigger>
            <TabsTrigger value="notifications" className="data-[state=active]:bg-primary">
              <Bell size={16} className="mr-2" />
              Notifications
            </TabsTrigger>
            <TabsTrigger value="security" className="data-[state=active]:bg-primary">
              <Lock size={16} className="mr-2" />
              Security
            </TabsTrigger>
            <TabsTrigger value="documents" className="data-[state=active]:bg-primary">
              <FileText size={16} className="mr-2" />
              Documents
            </TabsTrigger>
          </TabsList>

          {/* Profile Tab */}
          <TabsContent value="profile" className="space-y-6 mt-6">
            <Card className="bg-card border-border">
              <CardHeader>
                <CardTitle>Store Information</CardTitle>
                <CardDescription>Update your vendor profile details</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label htmlFor="store-name">Store Name</Label>
                    <Input id="store-name" defaultValue="TechStore Vietnam" className="bg-sidebar border-border" />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="slug">Store Slug</Label>
                    <Input id="slug" defaultValue="techstore-vietnam" className="bg-sidebar border-border" />
                  </div>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="description">Store Description</Label>
                  <textarea
                    id="description"
                    className="w-full p-3 bg-sidebar border border-border rounded-lg text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                    rows={4}
                    defaultValue="Premium electronics and technology accessories for Vietnamese market"
                  />
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label htmlFor="email">Contact Email</Label>
                    <Input
                      id="email"
                      type="email"
                      defaultValue="contact@techstore.vn"
                      className="bg-sidebar border-border"
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="phone">Phone Number</Label>
                    <Input id="phone" defaultValue="+84 (0) 28 3823 0000" className="bg-sidebar border-border" />
                  </div>
                </div>

                <Button className="bg-primary text-primary-foreground hover:bg-primary/90">Save Changes</Button>
              </CardContent>
            </Card>
          </TabsContent>

          {/* Notifications Tab */}
          <TabsContent value="notifications" className="space-y-6 mt-6">
            <Card className="bg-card border-border">
              <CardHeader>
                <CardTitle>Notification Preferences</CardTitle>
                <CardDescription>Manage how you receive notifications</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                {[
                  { label: "New Orders", desc: "Get notified when you receive new orders" },
                  { label: "Low Stock Alerts", desc: "Receive alerts when products fall below safety stock" },
                  { label: "Payment Notifications", desc: "Notifications about payment and settlements" },
                  { label: "Admin Messages", desc: "Important messages from platform administrators" },
                ].map((notification, idx) => (
                  <div key={idx} className="flex items-center justify-between p-3 bg-sidebar rounded-lg">
                    <div>
                      <p className="text-sm font-medium text-foreground">{notification.label}</p>
                      <p className="text-xs text-muted-foreground">{notification.desc}</p>
                    </div>
                    <input type="checkbox" defaultChecked className="w-4 h-4" />
                  </div>
                ))}

                <Button className="bg-primary text-primary-foreground hover:bg-primary/90">Save Preferences</Button>
              </CardContent>
            </Card>
          </TabsContent>

          {/* Security Tab */}
          <TabsContent value="security" className="space-y-6 mt-6">
            <Card className="bg-card border-border">
              <CardHeader>
                <CardTitle>Security Settings</CardTitle>
                <CardDescription>Manage your account security</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="password">Current Password</Label>
                  <Input id="password" type="password" className="bg-sidebar border-border" placeholder="••••••••" />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="new-password">New Password</Label>
                  <Input
                    id="new-password"
                    type="password"
                    className="bg-sidebar border-border"
                    placeholder="••••••••"
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="confirm-password">Confirm Password</Label>
                  <Input
                    id="confirm-password"
                    type="password"
                    className="bg-sidebar border-border"
                    placeholder="••••••••"
                  />
                </div>

                <Button className="bg-primary text-primary-foreground hover:bg-primary/90">Update Password</Button>
              </CardContent>
            </Card>
          </TabsContent>

          {/* Documents Tab */}
          <TabsContent value="documents" className="space-y-6 mt-6">
            <Card className="bg-card border-border">
              <CardHeader>
                <CardTitle>Business Documents</CardTitle>
                <CardDescription>Upload and manage your business verification documents</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                {[
                  { label: "Business License", status: "Verified" },
                  { label: "Tax Certificate", status: "Pending" },
                  { label: "Identity Verification", status: "Verified" },
                ].map((doc, idx) => (
                  <div key={idx} className="flex items-center justify-between p-3 bg-sidebar rounded-lg">
                    <div>
                      <p className="text-sm font-medium text-foreground">{doc.label}</p>
                      <p
                        className={`text-xs ${
                          doc.status === "Verified"
                            ? "text-green-500"
                            : doc.status === "Pending"
                              ? "text-yellow-500"
                              : "text-red-500"
                        }`}
                      >
                        Status: {doc.status}
                      </p>
                    </div>
                    <Button variant="outline" size="sm">
                      {doc.status === "Verified" ? "Update" : "Upload"}
                    </Button>
                  </div>
                ))}
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  )
}
