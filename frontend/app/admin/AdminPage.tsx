"use client"

import {ICategory} from "@/app/types/types";
import * as React from "react"
import {useEffect, useState} from "react"
import axios from "axios";
import {getColumns} from "@/app/admin/components/columns";
import {DataTable} from "@/app/admin/components/data-table";
import {Button} from "@/components/ui/button"
import {Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle, DialogTrigger,} from "@/components/ui/dialog"
import {zodResolver} from "@hookform/resolvers/zod"
import {useForm} from "react-hook-form"
import {z} from "zod"
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage,} from "@/components/ui/form"
import {Input} from "@/components/ui/input";
import {BsImages, BsPaperclip} from "react-icons/bs";
import Image from "next/image";
import {useToast} from "@/hooks/use-toast";

const MAX_FILE_SIZE = 1024 * 1024 * 5;
const ACCEPTED_IMAGE_MIME_TYPES = [
    "image/jpeg",
    "image/jpg",
    "image/png",
    "image/webp",
];

const getSchema = (isEditing: boolean) => {
    return z.object({
        name: z.string().min(1, {message: "Vui lòng nhập tên"}),
        file: isEditing ? z.instanceof(File)
                .optional()
                .refine((file) => {
                    console.log(file)
                    if (file) {
                        return file.size <= MAX_FILE_SIZE
                    }
                    return true
                }, "Kích thước hình ảnh tối đa là 5MB.")
                .refine((file) => {
                        if (file) {
                            return ACCEPTED_IMAGE_MIME_TYPES.includes(file.type)
                        }
                        return true
                    }, "Chỉ những định dạng ảnh: .jpg, .jpeg, .png và .webp mới được hỗ trợ",
                ) :
            z.instanceof(File, {message: "Vui lòng chọn hình ảnh"})
                .refine((file) => file.size <= MAX_FILE_SIZE, `Kích thước hình ảnh tối đa là 5MB.`)
                .refine((file) => ACCEPTED_IMAGE_MIME_TYPES.includes(file.type),
                    "Chỉ những định dạng ảnh: .jpg, .jpeg, .png và .webp mới được hỗ trợ",
                ),
    })
}


const AdminPage = () => {
    const [categoryList, setCategoryList] = useState<ICategory[]>([])
    const [selectedImage, setSelectedImage] = useState<File | null>(null);
    const {toast} = useToast()

    const DEFAULT_VALUES = {
        name: "",
        file: undefined,
    }

    const [openDialog, setOpenDialog] = useState(false)
    const [isEditing, setIsEditing] = useState(false)
    const formSchema = getSchema(isEditing)
    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: DEFAULT_VALUES,
    })
    const [isCreate, setIsCreate] = useState(false)
    const [categoryUpdate, setCategoryUpdate] = useState<ICategory | null>(null)

    const onUpdate = (categoryInput: ICategory) => {
        setCategoryUpdate(categoryInput)
        setIsEditing(true)
        setOpenDialog(true)
        form.setValue("name", categoryInput.name)
    }

    const columns = getColumns({onUpdate})

    async function onSubmit(values: z.infer<typeof formSchema>) {
        try {
            const formData = new FormData();
            formData.append("name", values.name);
            if (values.file) {
                formData.append("file", values.file);
            }
            console.log(formData)
            if (!isEditing) {
                const response = await axios.post(`${process.env.NEXT_PUBLIC_API_URL}/api/category/create-category`,
                    formData, {withCredentials: true})
                const data = await response.data;
                console.log(data)
                setCategoryList([...categoryList, data.category])
                toast({
                    className: "bg-green-500 text-white",
                    title: "Success",
                    description: data.message
                })
                setOpenDialog(false)
            } else {
                const response = await axios.patch(`${process.env.NEXT_PUBLIC_API_URL}/api/category/update-category-by-id/${categoryUpdate?.id}`,
                    formData, {withCredentials: true})
                const data = await response.data;
                setCategoryList(categoryList.map((category) => {
                    if (category.id === data.category.id) {
                        return data.category
                    } else {
                        return category
                    }
                }))
                toast({
                    className: "bg-green-500 text-white",
                    title: "Success",
                    description: data.message
                })
                setOpenDialog(false)
            }

        } catch (e: unknown) {
            toast({
                className: "bg-red-500 text-white",
                title: "Error",
                description: (e as Error).message
            })
        }
    }

    useEffect(() => {
        const fetchDataCategory = async () => {
            const response = await axios.get(`${process.env.NEXT_PUBLIC_API_URL}/api/category/get-all-categories`, {
                withCredentials: true,
            })
            const data = await response.data
            setCategoryList(data.categoryList)
        }
        fetchDataCategory()
    }, []);

    useEffect(() => {
        if (!openDialog) {
            setIsCreate(true)
            setIsEditing(false)
            form.reset(DEFAULT_VALUES)
            setSelectedImage(null)
        }
    }, [form, openDialog]);

    return (
        <div className="container mx-auto px-5">
            <Dialog open={openDialog} onOpenChange={setOpenDialog}>
                <DialogTrigger asChild>
                    <Button variant="outline"
                            className={"bg-green-500 hover:bg-green-400 text-white hover:text-white"}>
                        Thêm loại sản phẩm
                    </Button>
                </DialogTrigger>
                <DialogContent className="sm:max-w-[425px]">
                    <DialogHeader>
                        {
                            isEditing ? <DialogTitle>Chỉnh sửa loại sản phẩm</DialogTitle> :
                                <DialogTitle>Thêm loại sản phẩm</DialogTitle>
                        }
                    </DialogHeader>
                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                            <FormField
                                control={form.control}
                                name="name"
                                render={({field}) => (
                                    <FormItem>
                                        <FormLabel>Tên (*)</FormLabel>
                                        <FormControl>
                                            <Input placeholder="Nhập tên" {...field}
                                                   className={"focus:ring-inset"}/>
                                        </FormControl>
                                        <FormMessage/>
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="file"
                                render={({field}) => (
                                    <FormItem>
                                        <FormLabel>Hình ảnh</FormLabel>
                                        <FormControl>
                                            <div
                                                className={"flex items-center ring-inset ring-1 ring-[#f1f1f1] pr-1"}>
                                                <div
                                                    className={`flex md:flex-[1] h-[fit-content] md:p-4 md:flex-row
                                                                md:justify-between `}>
                                                    {
                                                        selectedImage && (
                                                            <div className="md:max-w-[200px]">
                                                                <Image
                                                                    src={URL.createObjectURL(selectedImage)}
                                                                    alt="Selected"
                                                                    width={100}
                                                                    height={100}
                                                                />
                                                            </div>
                                                        )
                                                    }
                                                    {
                                                        !isEditing && !selectedImage && (
                                                            <div
                                                                className="inline-flex items-center justify-between">
                                                                <div
                                                                    className="p-3 bg-slate-200 justify-center
                                                                    items-center flex">
                                                                    <BsImages size={56}/>
                                                                </div>
                                                            </div>
                                                        )
                                                    }
                                                    {
                                                        isEditing && !selectedImage && (
                                                            <div className="md:max-w-[200px]">
                                                                <Image
                                                                    src={categoryUpdate?.image ?? ""}
                                                                    alt="Selected"
                                                                    width={100}
                                                                    height={100}
                                                                />
                                                            </div>
                                                        )
                                                    }
                                                </div>

                                                <Button size="lg" type="button" className={"bg-white " +
                                                    "text-black hover:text-black hover:bg-white p-0"}>
                                                    <Input
                                                        type="file"
                                                        className="hidden"
                                                        id="fileInput"
                                                        accept="image/*"
                                                        onBlur={field.onBlur}
                                                        name={field.name}
                                                        onChange={(e) => {
                                                            const file = e.target.files
                                                            field.onChange(file && file[0]);
                                                            setSelectedImage(file?.[0] || null);
                                                        }}
                                                        ref={field.ref}
                                                    />
                                                    <label
                                                        htmlFor="fileInput"
                                                        className="cursor-pointer inline-flex items-center
                                                                w-full h-full rounded ring-ring px-5"
                                                    >
                                                        <BsPaperclip/>
                                                        <span className="whitespace-nowrap">
                                                            Choose your image
                                                        </span>
                                                    </label>
                                                </Button>
                                            </div>
                                        </FormControl>
                                        <FormMessage/>
                                    </FormItem>
                                )}
                            />
                            <DialogFooter>
                                <Button type="submit"
                                        className={"bg-green-500 hover:bg-green-400 text-white hover:text-white"}>
                                    {
                                        isEditing ? "Chỉnh sửa" : "Thêm"
                                    }
                                </Button>
                            </DialogFooter>
                        </form>
                    </Form>
                </DialogContent>
            </Dialog>
            <DataTable columns={columns} data={categoryList}/>
        </div>
    )
}

export default AdminPage