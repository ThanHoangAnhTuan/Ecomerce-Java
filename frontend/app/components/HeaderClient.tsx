"use client"

import Link from "next/link";
import Image from "next/image";
import Logo from "@/public/logo.png"
import {zodResolver} from "@hookform/resolvers/zod"
import {useForm} from "react-hook-form"
import {Button} from "@/components/ui/button"
import {FaHouse} from "react-icons/fa6";
import {
    Form,
    FormControl,
    FormField,
    FormItem, FormLabel,
    FormMessage,
} from "@/components/ui/form"
import {Input} from "@/components/ui/input"

import {z} from "zod"
import {FaRegSmileWink} from "react-icons/fa";
import {CiLocationOn, CiSearch, CiShoppingCart} from "react-icons/ci";
import {Separator} from "@/components/ui/separator";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
    DialogTrigger
} from "@/components/ui/dialog";
import {RadioGroup, RadioGroupItem} from "@/components/ui/radio-group";
import {Label} from "@/components/ui/label";
import React, {useEffect, useState} from "react";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select";
import jwt, {JwtPayload} from 'jsonwebtoken';
import {Popover, PopoverContent, PopoverTrigger} from "@/components/ui/popover";
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar";
import axios from "axios";
import {useRouter} from "next/navigation";
import {IAddress, IJwt} from "@/app/types/types";
import {Badge} from "@mui/material";


const formSchemaSearch = z.object({
    search: z.string(),
})

const formSchemaAddress = z.object({
    city: z.string(),
    district: z.string(),
    ward: z.string(),
})

const HeaderClient = ({token}: { token: string }) => {

    const address = "Q. 1, P. Bến Nghé, Hồ Chí Minh"
    const router = useRouter()

    const [radioValue, setRadioValue] = useState<string>("default")
    const [arrayCity, setArrayCity] = useState<IAddress[]>([])
    const [city, setCity] = useState("")

    const [arrayDistrict, setArrayDistrict] = useState<IAddress[]>([])
    const [district, setDistrict] = useState("")

    const [arrayWard, setArrayWard] = useState<IAddress[]>([])

    const [user, setUser] = useState<{ email?: string, username: string, image?: string, role?: string[] } | null>(null)

    useEffect(() => {
        const decoded: JwtPayload | string | null = jwt.decode(token)
        if (decoded) {
            const jwtPayload: IJwt = decoded as IJwt;
            setUser({
                email: jwtPayload.sub,
                username: jwtPayload.username,
                image: jwtPayload.image,
                role: jwtPayload.role,
            })
        } else {
            setUser(null)
        }
    }, [token]);

    useEffect(() => {
        const fetchDataCity = async () => {
            const response = await fetch("https://esgoo.net/api-tinhthanh/1/0.htm", {
                method: "GET",
            })
            const data = await response.json();
            if (data.error === 0) {
                setArrayCity(data.data)
                setArrayDistrict([])
                setArrayWard([])
            }
        }
        fetchDataCity()
    }, []);

    useEffect(() => {
        const fetchDataDistrict = async () => {
            const findCity = arrayCity.find((item: IAddress) => item.name === city)
            const response = await fetch(`https://esgoo.net/api-tinhthanh/2/${findCity?.id}.htm`)
            const data = await response.json();
            if (data.error === 0) {
                setArrayDistrict(data.data)
                setArrayWard([])
            }
        }
        if (city != "") {
            fetchDataDistrict()
        }
    }, [arrayCity, city]);

    useEffect(() => {
        const fetchDataWard = async () => {
            const findDistrict = arrayDistrict.find((item: IAddress) => item.name === district)
            const response = await fetch(`https://esgoo.net/api-tinhthanh/3/${findDistrict?.id}.htm`)
            const data = await response.json();
            if (data.error === 0) {
                setArrayWard(data.data)
            }
        }
        if (district != "") {
            fetchDataWard()
        }
    }, [arrayDistrict, district]);

    const formSearch = useForm<z.infer<typeof formSchemaSearch>>({
        resolver: zodResolver(formSchemaSearch),
        defaultValues: {
            search: "",
        },
    })

    function onSubmitSearch(values: z.infer<typeof formSchemaSearch>) {
        if (values.search.trim()) {
            router.push(`/search?keyword=${values.search.trim()}`)
        }
    }

    const formAddress = useForm<z.infer<typeof formSchemaAddress>>({
        resolver: zodResolver(formSchemaAddress),
        defaultValues: {
            city: "",
            district: "",
            ward: "",
        },
    })

    function onSubmitAddress(values: z.infer<typeof formSchemaAddress>) {
        console.log(values)
    }

    async function handlerLogout() {
        await axios.get(`${process.env.NEXT_PUBLIC_API_URL}/api/auth/logout`, {
            withCredentials: true
        })
        router.push('/')
    }

    return (
        <header className={"flex items-center justify-between px-20 py-5"}>
            <div>
                <Link href={""}>
                    <Image src={Logo} alt={"Logo"} width={100} height={100}/>
                </Link>
            </div>
            <div className={"border rounded overflow-hidden"}>
                <Form {...formSearch}>
                    <form onSubmit={formSearch.handleSubmit(onSubmitSearch)} className={"flex"}>
                        <FormField
                            control={formSearch.control}
                            name="search"
                            render={({field}) => (
                                <FormItem>
                                    <FormControl>
                                        <div className={"flex items-center space-y-0 pl-5"}>
                                            <CiSearch className={"size-5"}/>
                                            <Input
                                                className={"border-none bg-white shadow-none focus-visible:ring-0 " +
                                                    "w-[600px] max-w-[600px] rounded-none"}
                                                placeholder="Search ..." {...field} />
                                            <Separator orientation="vertical" className={"h-[20px]"}/>
                                        </div>
                                    </FormControl>
                                    <FormMessage/>
                                </FormItem>
                            )}
                        />
                        <Button type="submit"
                                className={"rounded-none bg-white text-blue-700 shadow-none hover:bg-[#0A68FF33]"}>
                            Tìm kiếm
                        </Button>
                    </form>
                </Form>
            </div>
            <div className={"flex flex-col"}>
                <div className={"flex gap-x-2"}>
                    <Link href={"/"} className={"text-[#000000cc] flex items-center px-4 py-2 rounded hover:bg-[#0A68FF33]"}>
                        <FaHouse className={"mr-2 size-5 text-[#000000cc]"}/>
                        Trang chủ
                    </Link>
                    {
                        user && (
                            <Popover>
                                <PopoverTrigger asChild>
                                    <button
                                        className={"flex items-center bg-white hover:bg-[#0A68FF33] shadow-none px-2 py-1 rounded"}>
                                        <Avatar>
                                            <AvatarImage src={user.image} alt={user.username}/>
                                            <AvatarFallback className={"text-[#000000cc]"}>
                                                {user.username.split(" ").map(word => word[0]).join("")}
                                            </AvatarFallback>
                                        </Avatar>
                                        <h1 className={"text-[#000000cc] ml-1"}>{user.username}</h1>
                                    </button>
                                </PopoverTrigger>
                                <PopoverContent className={"w-fit"}>
                                    <Link href={"/my-info"} className={"block hover:bg-[#f1f1f1] px-5 py-2 rounded"}>
                                        Tài khoản của tôi
                                    </Link>
                                    {
                                        user.role?.includes("BUYER") && (
                                            <Link href={"/my-buyer-order"} className={"block hover:bg-[#f1f1f1] px-5 py-2 rounded"}>
                                                Đơn mua
                                            </Link>
                                        )
                                    }
                                    {
                                        user.role?.includes("SELLER") && (
                                            <Link href={"/my-seller-order"} className={"block hover:bg-[#f1f1f1] px-5 py-2 rounded"}>
                                                Đơn bán
                                            </Link>
                                        )
                                    }
                                    {
                                        user.role?.includes("SELLER") && (
                                            <Link href={"/my-shop"} className={"block hover:bg-[#f1f1f1] px-5 py-2 rounded"}>
                                                Quản lý sản phẩm
                                            </Link>
                                        )
                                    }
                                    {
                                        user.role?.includes("ADMIN") && (
                                            <Link href={"/admin"} className={"block hover:bg-[#f1f1f1] px-5 py-2 rounded"}>
                                                Quản trị viên
                                            </Link>
                                        )
                                    }
                                    <Button
                                        onClick={() => handlerLogout()}
                                        className={"h-[40px] block hover:bg-[#f1f1f1] px-5 py-2 rounded w-full " +
                                            "text-start text-[#0A0A0A] text-[16px] font-normal"}
                                        variant={"reset"}>
                                        Đăng xuất
                                    </Button>
                                </PopoverContent>
                            </Popover>
                        )
                    }
                    {
                        !user && (
                            <Link href={"/login"} className={"flex items-center px-4 py-2 rounded hover:bg-[#0A68FF33]"}>
                                <FaRegSmileWink className={"mr-2 size-5"}/>
                                Tài khoản
                            </Link>
                        )
                    }
                    <Link href={"/order-details"} className={"flex items-center px-4 py-2 rounded hover:bg-[#0A68FF33]"}>
                        <Badge color="info" badgeContent={1}>
                            <CiShoppingCart className={"size-5"}/>
                        </Badge>
                    </Link>
                </div>
                <div>
                    <Dialog>
                        <DialogTrigger asChild>
                            <Button className={"bg-white text-black shadow-none hover:bg-white"}>
                                <span className={"opacity-50 flex items-center"}>
                                    <CiLocationOn/>
                                    Giao đến:</span>
                                <span className={"underline"}>{address}</span>
                            </Button>
                        </DialogTrigger>
                        <DialogContent className="sm:max-w-[425px]">
                            <DialogHeader>
                                <DialogTitle className={"text-center"}>Địa chỉ giao hàng</DialogTitle>
                                <DialogDescription>
                                    Hãy chọn địa chỉ nhận hàng để được dự báo thời gian giao hàng cùng phí đóng gói, vận
                                    chuyển một cách chính xác nhất.
                                </DialogDescription>
                            </DialogHeader>
                            <Button className={"bg-[#FDD835] text-black hover:bg-[#FDD835]"}>Đăng nhập để chọn
                                địa chỉ giao hàng</Button>
                            <div className={"flex items-center my-5"}>
                                <Separator className={"flex-1"}/>
                                <span className={"px-5"}>Hoặc</span>
                                <Separator className={"flex-1"}/>
                            </div>
                            <RadioGroup defaultValue={radioValue} onValueChange={(e) => {
                                setRadioValue(e)
                            }}>
                                <div className="flex items-center space-x-2">
                                    <RadioGroupItem value="default" id="r1"/>
                                    <Label htmlFor="r1">{address}</Label>
                                </div>
                                <div className="flex items-center space-x-2">
                                    <RadioGroupItem value="other" id="r2"/>
                                    <Label htmlFor="r2">Chọn khu vực giao hàng khác</Label>
                                </div>
                            </RadioGroup>
                            {
                                radioValue === "other" && (
                                    <Form {...formAddress}>
                                        <form onSubmit={formAddress.handleSubmit(onSubmitAddress)}
                                              className="w-2/3 space-y-6">
                                            <FormField
                                                control={formAddress.control}
                                                name="city"
                                                render={({field}) => (
                                                    <FormItem>
                                                        <FormLabel>Tỉnh/Thành phố</FormLabel>
                                                        <Select onValueChange={(e) => setCity(e)}
                                                                defaultValue={field.value}>
                                                            <FormControl>
                                                                <SelectTrigger>
                                                                    <SelectValue
                                                                        placeholder="Vui lòng chọn tỉnh/thành phố"/>
                                                                </SelectTrigger>
                                                            </FormControl>
                                                            <SelectContent>
                                                                {
                                                                    arrayCity.map((city: IAddress) => (
                                                                        <SelectItem key={city.id}
                                                                                    value={city.name}>{city.name}</SelectItem>
                                                                    ))
                                                                }
                                                            </SelectContent>
                                                        </Select>
                                                        <FormMessage/>
                                                    </FormItem>
                                                )}
                                            />
                                            <FormField
                                                control={formAddress.control}
                                                name="district"
                                                render={({field}) => (
                                                    <FormItem>
                                                        <FormLabel>Quận/Huyện</FormLabel>
                                                        <Select onValueChange={(e) => setDistrict(e)}
                                                                defaultValue={field.value}>
                                                            <FormControl>
                                                                <SelectTrigger>
                                                                    <SelectValue placeholder="Vui lòng chọn quận/huyện"/>
                                                                </SelectTrigger>
                                                            </FormControl>
                                                            <SelectContent>
                                                                {
                                                                    arrayDistrict.map((district: IAddress) => (
                                                                        <SelectItem key={district.id}
                                                                                    value={district.name}>{district.name}</SelectItem>
                                                                    ))
                                                                }
                                                            </SelectContent>
                                                        </Select>
                                                        <FormMessage/>
                                                    </FormItem>
                                                )}
                                            />
                                            <FormField
                                                control={formAddress.control}
                                                name="ward"
                                                render={({field}) => (
                                                    <FormItem>
                                                        <FormLabel>Phường/Xã</FormLabel>
                                                        <Select onValueChange={(e) => setDistrict(e)}
                                                                defaultValue={field.value}>
                                                            <FormControl>
                                                                <SelectTrigger>
                                                                    <SelectValue placeholder="Vui lòng chọn phường/xã"/>
                                                                </SelectTrigger>
                                                            </FormControl>
                                                            <SelectContent>
                                                                {
                                                                    arrayWard.map((ward: IAddress) => (
                                                                        <SelectItem key={ward.id}
                                                                                    value={ward.name}>{ward.name}</SelectItem>
                                                                    ))
                                                                }
                                                            </SelectContent>
                                                        </Select>
                                                        <FormMessage/>
                                                    </FormItem>
                                                )}
                                            />
                                            <Button type="submit">Giao đến địa chỉ này</Button>
                                        </form>
                                    </Form>
                                )
                            }
                        </DialogContent>
                    </Dialog>
                </div>
            </div>
        </header>
    )
}

export default HeaderClient