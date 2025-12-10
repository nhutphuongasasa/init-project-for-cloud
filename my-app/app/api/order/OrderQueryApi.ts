import axios from "axios"

export async function  getMyOrder() {
    const res = await axios.get(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-query`, {
        withCredentials: true
    });
    return res.data;
}

export async function getAllOrders() {
    const res = await axios.get(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-query/all`, {
        withCredentials: true
    });
    return res.data;
}

export async function getOrderDetail() {
    const id = "9660a922-415d-4e55-8bb5-97db366b882e"
    const res = await axios.get(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-query/admin/${id}`, {
        withCredentials: true
    });
    return res.data;
}

export async function getMyOrderDetail() {
    const id = "9660a922-415d-4e55-8bb5-97db366b882e"
    const res = await axios.get(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/order/orders-query/my/${id}`, {
        withCredentials: true
    });
    return res.data;
}

// export async function searchOrder() {

    
// }
