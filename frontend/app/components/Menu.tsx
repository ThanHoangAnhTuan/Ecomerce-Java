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
                    <Link key={category.id}
                          href={`/category/${removeVietnameseTonesAndReplaceSpaces(category.name)}/${category.id}`}
                          className={"flex items-center hover:bg-[#f1f1f1] rounded min-w-40 px-2 min-h-fit"}
                    >
                        <Image className={"mix-blend-darken mr-2"} src={category.image} alt={category.name} width={40} height={40}/>
                        <h1 className={""}>{category.name}</h1>
                    </Link>

                ))
            }
        </div>
    )
}

export default Menu