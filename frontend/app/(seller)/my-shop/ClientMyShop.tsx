"use client"

import {ICategory, IJwt, IProduct} from "@/app/types/types";
import * as React from "react"
import {useEffect, useState} from "react"
import axios from "axios";
import {getColumns} from "@/app/(seller)/my-shop/components/columns";
import {DataTable} from "@/app/(seller)/my-shop/components/data-table";
import {Button} from "@/components/ui/button"
import {Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle, DialogTrigger,} from "@/components/ui/dialog"
import {zodResolver} from "@hookform/resolvers/zod"
import {useForm} from "react-hook-form"
import {z} from "zod"
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage,} from "@/components/ui/form"
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select";
import {ScrollArea} from "@/components/ui/scroll-area";
import {Input} from "@/components/ui/input";
import {Textarea} from "@/components/ui/textarea";
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

const getSchema = (isEditing:boolean) => {
    return z.object({
        category: z
            .string().min(1, {message: "Vui lòng chọn loại sản phẩm"}),
        name: z.string().min(1, {message: "Vui lòng nhập tên"}),
        description: z.string(),
        price: z.preprocess((value) => (value === "" ? undefined : Number(value)),
            z.number({
                required_error: "Vui lòng nhập giá",
                invalid_type_error: "Giá phải là một số"
            }).min(1, {message: "Vui lòng nhập giá"})),
        inventory: z.preprocess((value) => (value === "" ? undefined : Number(value)),
            z.number()),
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


const ClientMyShop = () => {
    const [productList, setProductList] = useState<IProduct[]>([])
    const [categoryList, setCategoryList] = useState<ICategory[]>([])
    const [selectedImage, setSelectedImage] = useState<File | null>(null);
    const {toast} = useToast()

    const DEFAULT_VALUES = {
        category: "",
        name: "",
        description: "",
        price: 0,
        inventory: 0,
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
    const [productUpdate, setProductUpdate] = useState<IProduct | null>(null)

    const onUpdate = (productInput: IProduct) => {
        setProductUpdate(productInput)
        setIsEditing(true)
        setOpenDialog(true)
        form.setValue("category", productInput.category.name)
        form.setValue("name", productInput.name)
        form.setValue("description", productInput.description ? productInput.description : "")
        form.setValue("price", productInput.price)
        form.setValue("inventory", productInput.inventory)
    }

    const onDelete = async (productInput: IProduct) => {
        try {
            const response = await axios.delete(`${process.env.NEXT_PUBLIC_API_URL}/api/products/delete-product/${productInput.id}`, {
                withCredentials: true
            })
            const data = await response.data;
            setProductList(productList.filter((product: IProduct) => product.id !== productInput.id))
            toast({
                className: "bg-green-500 text-white",
                title: "Success",
                description: data.message
            })
        } catch (e: unknown) {
            toast({
                className: "bg-red-500 text-white",
                title: "Error",
                description: (e as Error).message
            })
        }
    }

    const columns = getColumns({onUpdate, onDelete})

    async function onSubmit(values: z.infer<typeof formSchema>) {
        try {
            const findCategory = categoryList.find((category) => category.name === values.category)

            const formData = new FormData();
            formData.append("categoryId", String(findCategory?.id));
            formData.append("name", values.name);
            formData.append("description", values.description);
            formData.append("price", String(values.price));
            formData.append("inventory", String(values.inventory));
            console.log(values.file)
            if (values.file) {
                formData.append("file", values.file); // thêm tệp
            }
            if (!isEditing) {
                const response = await axios.post(`${process.env.NEXT_PUBLIC_API_URL}/api/products/create-product`,
                    formData, {withCredentials: true})
                const data = await response.data;
                setProductList([...productList, data.product])
                toast({
                    className: "bg-green-500 text-white",
                    title: "Success",
                    description: data.message
                })
                setOpenDialog(false)
            } else {
                const response = await axios.patch(`${process.env.NEXT_PUBLIC_API_URL}/api/products/update-product/${productUpdate?.id}`,
                    formData, {withCredentials: true})
                const data = await response.data;
                setProductList(productList.map((product) => {
                    if (product.id === data.product.id) {
                        return data.product
                    } else {
                        return product
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
        const fetchDataProduct = async () => {
            const response = await axios.get(`${process.env.NEXT_PUBLIC_API_URL}/api/products/get-product-by-seller-id`, {
                withCredentials: true,
            })
            const data = await response.data
            setProductList(data.productList)
        }
        fetchDataProduct()
    }, []);

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
                        Thêm sản phẩm
                    </Button>
                </DialogTrigger>
                <DialogContent className="sm:max-w-[425px]">
                    <ScrollArea className="h-[500px] p-4">
                        <DialogHeader>
                            {
                                isEditing ? <DialogTitle>Chỉnh sửa sản phẩm</DialogTitle> :
                                    <DialogTitle>Thêm sản phẩm</DialogTitle>
                            }
                        </DialogHeader>
                        <Form {...form}>
                            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                                <FormField
                                    control={form.control}
                                    name="category"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>Loại sản phẩm (*)</FormLabel>
                                            <Select onValueChange={field.onChange}
                                                    defaultValue={field.value}>
                                                <FormControl>
                                                    <SelectTrigger className={"focus:ring-inset"}>
                                                        <SelectValue
                                                            placeholder="Chọn loại sản phẩm"/>
                                                    </SelectTrigger>
                                                </FormControl>
                                                <SelectContent className="overflow-y-auto max-h-[10rem]">
                                                    {
                                                        categoryList.map((category) => (
                                                            <SelectItem key={category.id}
                                                                        value={category.name}>{category.name}</SelectItem>
                                                        ))
                                                    }
                                                </SelectContent>
                                            </Select>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />
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
                                    name="description"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>Mô tả</FormLabel>
                                            <FormControl>
                                                <Textarea
                                                    placeholder="Nhập mô tả"
                                                    className="resize-none focus:ring-inset"
                                                    {...field}
                                                />
                                            </FormControl>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="price"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>Giá (*)</FormLabel>
                                            <FormControl>
                                                <Input type={"number"} placeholder="Nhập giá" {...field}
                                                       onFocus={(e) =>
                                                           e.target.addEventListener("wheel", (e) =>
                                                               e.preventDefault(), {passive: false})}
                                                       className={"focus:ring-inset"}/>
                                            </FormControl>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="inventory"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>Tồn kho</FormLabel>
                                            <FormControl>
                                                <Input type={"number"} placeholder="Nhập số lượng tồn kho" {...field}
                                                       onFocus={(e) =>
                                                           e.target.addEventListener("wheel", (e) =>
                                                               e.preventDefault(), {passive: false})}
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
                                                                        src={productUpdate?.image ?? ""}
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
                    </ScrollArea>
                </DialogContent>
            </Dialog>
            <DataTable columns={columns} data={productList}/>
        </div>
    )
}

export default ClientMyShop