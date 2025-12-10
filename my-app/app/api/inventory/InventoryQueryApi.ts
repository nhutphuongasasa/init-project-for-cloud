import axios from "axios"

export async function  getStockDetail() {
    const variantId = "66666666-6666-6666-6666-666666666666"
    const res = await axios.get(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/inventory/inventory-query/stock/${variantId}`, {
        withCredentials: true
    })

    return res.data;
}

export async function getMovement() {
    const variantId = "66666666-6666-6666-6666-666666666666"
    const res = await axios.get(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/inventory/inventory-query/movements/${variantId}?page=0&size=20&sort=createdAt,desc`, {
        withCredentials: true
    })

    return res.data;
}

export async function getStockReport() {
    const res = await axios.get(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/inventory/inventory-query/report`, {
        withCredentials: true
    })

    return res.data;
}

export async function getMyWarehouses() {
    const res = await axios.get(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/inventory/inventory-query/warehouses`, {
        withCredentials: true
    })

    return res.data;
}