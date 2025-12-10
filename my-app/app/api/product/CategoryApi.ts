import axios from "axios";

export async function createCategory(){
    const res = await axios.post(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/product/category`,
        {
            name: "phuong",
            slug: "phuong",
            iconUrl: "phuong",
            parentId: "",
        },
        {
        withCredentials: true,
        }
    );

    console.log("res.data", res.data);

    return res.data;
}

export async function getAllCategories() {
   const res = await axios.get(
       `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/product/category/all`,
       {
       withCredentials: true,
       }
   );

   console.log("res.data", res.data);

   return res.data;
}

export async function getCategoryBySlug() {
    const slug = "phuong"
   const res = await axios.get(
       `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/product/category/slug/${slug}`,
       {
       withCredentials: true,
       }
   );

   console.log("res.data", res.data);

   return res.data;
}

export async function deleteCategory() {
    const categoryId = 6a420bf5-7b65-4d5f-89d8-31761b34e4f2"
   const res = await axios.delete(
       `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/product/category/${categoryId}`,
       {
       withCredentials: true,
       }
   );

   console.log("res.data", res.data);

   return res.data;
}
