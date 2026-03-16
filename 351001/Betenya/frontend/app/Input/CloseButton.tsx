import { X } from 'lucide-react'

interface CloseButtonProps {
    onClick: () => void;
    size?: number;
    className?: string;
}

export const CloseButton = ({
                                onClick,
                                size = 20,
                                className = ""
                            }: CloseButtonProps) => {
    return (
        <button
            onClick={onClick}
            className={`
                p-2 rounded-lg hover:bg-gray-100 dark:hover:bg-zinc-800 
                text-gray-500 dark:text-zinc-400
                transition-colors cursor-pointer
                ${className}
            `}
            aria-label="Close"
        >
            <X size={size} />
        </button>
    )
}