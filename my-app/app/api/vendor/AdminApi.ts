import axios from "axios";

export async function approveVendor() {
    const vendorId = "e9a8134f-9041-433d-8a3e-ad389eb586a3";
    const res = await axios.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/vendor/admin/approve/${vendorId}`,
        {},
        {
        withCredentials: true,
        }
    );

    console.log("res.data", res.data);

    return res.data;
}

export async function getPendingVendors() {
    const res = await axios.get(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/vendor/admin/pending`,
        {
        withCredentials: true,
        }
    );

    console.log("res.data", res.data);

    return res.data;
}

export async function searchVendor() {
    const res = await axios.get(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/vendor/admin/search?status=PENDING&page=0&size=10`,
        {
        withCredentials: true,
        }
    );

    console.log("res.data", res.data);

    return res.data;
}

export async function getAllVendor() {
    const res = await axios.get(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/vendor/admin/all?status=PENDING&page=0&size=10`,
        {
        withCredentials: true,
        }
    );

    console.log("res.data", res.data);

    return res.data;
}
    