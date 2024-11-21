"use client"

import {useEffect, useState} from "react";
import Image from "next/image";
import Link from "next/link";
import {removeVietnameseTonesAndReplaceSpaces} from "@/app/utils/removeVietnameseTonesAndReplaceSpaces";
import {ICategory} from "@/app/types/types";
import axios from "axios";

const Menu = () => {
    const [categoryList, setCategoryList] = useState([])
    useEffect(() => {
        console.log("fetchDataCategory NEXT_PUBLIC_API_URL")
        console.log(process.env.NEXT_PUBLIC_API_URL)
        console.log("fetchDataCategory SERVER_API_URL")
        console.log(process.env.SERVER_API_URL)

        console.log(`${process.env.NEXT_PUBLIC_API_URL}/api/category/get-all-categories`)
        const fetchDataCategory = async () => {
            const response = await axios.get(`${process.env.NEXT_PUBLIC_API_URL}/api/category/get-all-categories`, {
                withCredentials: true
            })
            const data = await response.data;
            setCategoryList(data.categoryList)
        }
        fetchDataCategory()
    }, []);

    return (
        <div className={"bg-white rounded px-5 py-2"}>
            <h1>Danh mục</h1>
            {
                categoryList.map((category:ICategory) => (
                    <Link key={category.id} href={`/category/${removeVietnameseTonesAndReplaceSpaces(category.name)}/${category.id}`} className={"flex items-center hover:bg-[#c5c5c5] rounded py-2"}>
                        <Image src={category.image} alt={category.name} width={40} height={40}/>
                        <h1>{category.name}</h1>
                    </Link>

                ))
            }
        </div>
    )
}

export default Menu