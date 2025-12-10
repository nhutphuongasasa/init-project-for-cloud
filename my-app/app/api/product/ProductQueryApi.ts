import axios from "axios";

export async function getMyProducts() {
    const res = await axios.get(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/product/products-query/my`,
        {
        withCredentials: true,
        }
    );

    console.log("res.data", res.data);

    return res.data;
}

export async function getMyActiveProducts() {
    const res = await axios.get(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/product/products-query/my/active`,
        {
        withCredentials: true,
        }
    );

    console.log("res.data", res.data);

    return res.data;
}


export async function getproductById() {
    const productId ="9ff2239e-34cf-423c-a5f0-51d0a35c7f1e"
    const res = await axios.get(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/product/products-query/${productId}`,
        {
        withCredentials: true,
        }
    );

    console.log("res.data", res.data);

    return res.data;
}


export async function searchMyproduct() {
        const categoryId = "1b21b65a-faad-4626-8e8d-aa98907431e6"
        // const status = ""
        // const price = ""
        // const originalPrice = ""
        const page = "0"
        const size = "1"
    const res = await axios.get(

        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/product/products-query/my/search?categoryId=${categoryId}&page=${page}&size=${size}`,

        {
        withCredentials: true,
        }
    );

    console.log("res.data", res.data);

    return res.data;
}

export async function searchAllProductAdmin(){
        // const categoryId = ""
        // const status = ""
        // const price = ""
        // const originalPrice = ""
        // const vendorId = ""
        const slug = "phuong"
        const page = "0"
        const size = "1"
    const res = await axios.get(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/product/products-query/search?slug=${slug}&page=${page}&size=${size}`,
        {
        withCredentials: true,
        }
    );

    console.log("res.data", res.data);

    return res.data;
}

export async function getAllProductAdmin(){
    const res = await axios.get(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/product/products-query`,
        {
        withCredentials: true,
        }
    );

    console.log("res.data", res.data);

    return res.data;
}

