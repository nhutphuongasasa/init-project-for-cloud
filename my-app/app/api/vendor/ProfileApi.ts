import axios from "axios";

export async function updateBasicInfo() {
    const res = await axios.put(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/vendor/profile/basic-info`,
        {
            name: "phuong",
            slug: "phuong",
            logoUrl: "phuong",
            description: "phuong",
        },
        {
        withCredentials: true,
        }
    );

    console.log("res.data", res.data);

    return res.data;
}

export async function updateProfile(){
    const res = await axios.put(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/vendor/profile`,
        {
            address: "phuong",
            phone: "phuong",
            email: "phuong",
            taxCode: "phuong",
            websiteUrl: "phuong",
            socialLinks: {
                facebook: "phuong",
                instagram: "phuong",
                twitter: "phuong",
                youtube: "phuong",
            },
        },
        {
        withCredentials: true,
        }
    );

    console.log("res.data", res.data);

    return res.data;
}