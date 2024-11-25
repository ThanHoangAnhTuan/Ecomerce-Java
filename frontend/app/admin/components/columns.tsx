import {ColumnDef} from "@tanstack/react-table"
import {ICategory} from "@/app/types/types";
import Image from "next/image";
import React from "react";
import DataTableRowActions from "@/app/admin/components/row-action";

interface ColumnsProps {
    onUpdate: (product: ICategory) => void,
}

export const getColumns = ({onUpdate}: ColumnsProps): ColumnDef<ICategory>[] => [
    {
        accessorKey: "id",
        header: "Id",
        cell: ({row},) => (
            <div className="capitalize">{row.getValue("id")}</div>
        ),
    },
    {
        accessorKey: "name",
        header: "Tên",
        cell: ({row}) => (
            <div className="capitalize">{row.getValue("name")}</div>
        ),
    },
    {
        accessorKey: "image",
        header: "Hình ảnh",
        cell: ({row}) => (
            <div className="capitalize">
                <Image src={row.getValue("image")} alt={"Hình ảnh"} width={100} height={100}/>
            </div>
        ),
    },
    {
        id: "actions",
        enableHiding: false,
        cell: ({row}) =>
            <DataTableRowActions row={row}
                                 onUpdate={onUpdate}/>,
    },
]
