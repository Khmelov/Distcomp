import {Button} from "@/app/Input/Button";
import {RequestMethod} from "@/app/resources/data";
import {getBcBlockColor, getBorderColor} from "@/app/resources/utils";

interface RequestItemProps {
    method: RequestMethod;
    url: string;
    onClick: () => void;
}

export const RequestItem = ({
    method,
    url,
    onClick
} : RequestItemProps) => {
    return (
        <button className={`flex items-center p-3 border flex-1 rounded-xl justify-between
                ${getBorderColor(method)}
                ${getBcBlockColor(method)}
                transition-transform duration-200 ease-in-out
                hover:scale-[1.02] active:scale-[0.98]
                hover:cursor-pointer
            `}
            onClick={onClick}
        >
            <span className="text-black dark:text-zinc-50">{url}</span>
            <Button method={method} />
        </button>
    );
}