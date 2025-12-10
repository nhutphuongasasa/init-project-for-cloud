// components/Sidebar.tsx
"use client";

import { useRouter, usePathname } from "next/navigation";
import { useState } from "react";
import { LayoutGrid, Package, Warehouse, ShoppingCart, Users, Settings, LogOut, Menu, X } from "lucide-react";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";

interface SidebarProps {
  userType: "admin" | "vendor"
}

export function Sidebar({ userType }: SidebarProps) {
  const router = useRouter();
  const pathname = usePathname();
  const [expanded, setExpanded] = useState(true);

  const isAdmin = userType === "admin";

  const menuItems = isAdmin
    ? [
        { label: "Dashboard", icon: LayoutGrid, href: "/admin/dashboard" },
        { label: "Vendors", icon: Users, href: "/admin/vendors" },
        { label: "Products", icon: Package, href: "/admin/products" },
        { label: "Warehouses", icon: Warehouse, href: "/admin/warehouses" },
        { label: "Orders", icon: ShoppingCart, href: "/admin/orders" },
        { label: "Settings", icon: Settings, href: "/admin/settings" },
      ]
    : [
        { label: "Dashboard", icon: LayoutGrid, href: "/vendor/dashboard" },
        { label: "Products", icon: Package, href: "/vendor/products" },
        { label: "Inventory", icon: Warehouse, href: "/vendor/inventory" },
        { label: "Orders", icon: ShoppingCart, href: "/vendor/orders" },
        { label: "Settings", icon: Settings, href: "/vendor/settings" },
      ];

  const handleLogout = () => {
    // XÓA SẠCH COOKIE + LOCAL
    document.cookie = "JSESSIONID=; path=/; expires=Thu, 01 Jan 1970 00:00:01 GMT;";
    document.cookie = "JSESSIONID=; path=/; expires=Thu, 01 Jan 1970 00:00:01 GMT; domain=localhost";
    localStorage.clear();
    router.push("/");
  };

  // TOÀN BỘ PHẦN UI BÊN DƯỚI LÀ CỦA BẠN – KHÔNG ĐỘNG GÌ HẾT
  return (
    <>
      <div className="md:hidden fixed top-0 left-0 right-0 h-16 bg-sidebar border-b border-sidebar-border flex items-center px-4 z-40">
        <Button variant="ghost" size="icon" onClick={() => setExpanded(!expanded)}>
          {expanded ? <X size={20} /> : <Menu size={20} />}
        </Button>
        <span className="ml-4 font-bold text-sidebar-foreground">Warehouse Hub</span>
      </div>

      <aside className={cn(
        "fixed left-0 top-0 h-screen bg-sidebar border-r border-sidebar-border transition-all duration-300 z-50 md:z-40",
        expanded ? "w-64" : "w-20",
        "md:relative md:top-0 flex flex-col",
      )}>
        <div className="p-6 border-b border-sidebar-border hidden md:block">
          <h2 className={cn("font-bold text-sidebar-foreground transition-all", expanded ? "text-xl" : "text-xs text-center")}>
            {expanded ? "Warehouse" : "WH"}
          </h2>
        </div>

        <nav className="flex-1 p-4 space-y-2 mt-16 md:mt-0">
          {menuItems.map((item) => {
            const Icon = item.icon;
            const isActive = pathname === item.href;
            return (
              <Button
                key={item.href}
                variant={isActive ? "default" : "ghost"}
                className={cn("w-full justify-start", isActive && "bg-sidebar-primary text-sidebar-primary-foreground")}
                onClick={() => router.push(item.href)}
              >
                <Icon size={20} />
                {expanded && <span className="ml-3">{item.label}</span>}
              </Button>
            );
          })}
        </nav>

        <div className="p-4 border-t border-sidebar-border">
          <Button
            variant="ghost"
            className="w-full justify-start text-destructive hover:bg-destructive/10 hover:text-destructive"
            onClick={handleLogout}
          >
            <LogOut size={20} />
            {expanded && <span className="ml-3">Logout</span>}
          </Button>
        </div>
      </aside>

      {expanded && <div className="fixed inset-0 bg-black/50 z-30 md:hidden" onClick={() => setExpanded(false)} />}
    </>
  );
}