import {Row} from "@tanstack/table-core";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuTrigger
} from "@/components/ui/dropdown-menu";
import {Button} from "@/components/ui/button";
import {MoreHorizontal} from "lucide-react";
import React from "react";

interface DataTableRowActionsProps<TData> {
    row: Row<TData>,
    onUpdate: (value: TData) => void,
}

const DataTableRowActions = <TData,>({row, onUpdate}: DataTableRowActionsProps<TData>)=> {
    return (
        <DropdownMenu>
            <DropdownMenuTrigger asChild>
                <Button variant="ghost" className="h-8 w-8 p-0">
                    <span className="sr-only">Open menu</span>
                    <MoreHorizontal/>
                </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
                <DropdownMenuLabel>Actions</DropdownMenuLabel>
                <DropdownMenuItem onClick={() => onUpdate(row.original)}>Chỉnh sửa trạng thái</DropdownMenuItem>
            </DropdownMenuContent>
        </DropdownMenu>
    )
}

export default DataTableRowActions