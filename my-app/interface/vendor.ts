export type VendorStatus = "ACTIVE" | "DRAFT" | "INACTIVE" | "BANNED" | "SUSPENDED"

export interface Vendor {
    id: string;
    name: string;
    slug: string;
    description: string;
    logoUrl: string;
    status: string;
    joinedAt: string;
    profile: VendorProfile;
}

export interface VendorProfile{
    address: string;
    phone: string;
    email: string,
    websiteUrl: string;
    socialLinks: Record<string, string>;
}

export const statusConfig: Record<VendorStatus, { 
    bg: string; 
    text: string; 
    label: string 
}> = {
  ACTIVE:   { bg: "bg-green-500/10", text: "text-green-500", label: "ACTIVE" },
  DRAFT:    { bg: "bg-yellow-500/10", text: "text-yellow-500", label: "DRAFT" },
  INACTIVE: { bg: "bg-gray-500/10", text: "text-gray-500", label: "INACTIVE" },
  BANNED:   { bg: "bg-red-500/10", text: "text-red-500", label: "BANNED" },
  SUSPENDED:{ bg: "bg-rose-500/10", text: "text-rose-500", label: "SUSPENDED" },
}