"use client"

import { useState, useEffect, useRef } from 'react'
import { ChevronDown } from 'lucide-react'

interface SelectProps {
    items: string[];
    value?: string;
    onChange?: (value: string) => void;
    placeholder?: string;
}

export const Select = ({
                           items,
                           value: propValue,
                           onChange,
                           placeholder = "Select version..."
                       }: SelectProps) => {
    const [isOpen, setIsOpen] = useState(false)
    const [selectedValue, setSelectedValue] = useState<string | undefined>(propValue || items[0])
    const [isMounted, setIsMounted] = useState(false)
    const selectRef = useRef<HTMLDivElement>(null)

    // Синхронизация с внешним value
    useEffect(() => {
        if (propValue !== undefined) {
            setSelectedValue(propValue)
        }
    }, [propValue])

    // Закрытие при клике вне компонента
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (selectRef.current && !selectRef.current.contains(event.target as Node)) {
                setIsOpen(false)
            }
        }

        document.addEventListener('mousedown', handleClickOutside)
        setIsMounted(true)
        return () => document.removeEventListener('mousedown', handleClickOutside)
    }, [])

    const handleSelect = (item: string) => {
        setSelectedValue(item)
        onChange?.(item)
        setIsOpen(false)
    }

    // Если компонент еще не монтирован или нет элементов
    if (!isMounted || items.length === 0) {
        return (
            <div className="relative w-48">
                <div className="px-4 py-2.5 rounded-xl border border-gray-300 dark:border-zinc-700 bg-white dark:bg-zinc-900 text-gray-400 dark:text-zinc-500">
                    Loading...
                </div>
            </div>
        )
    }

    return (
        <div ref={selectRef} className="relative w-48">
            {/* Триггер выпадающего списка */}
            <button
                type="button"
                onClick={() => setIsOpen(!isOpen)}
                className={`
                    w-full px-4 py-2.5 rounded-xl border 
                    flex items-center justify-between gap-2
                    transition-all duration-200
                    hover:shadow-md
                    ${isOpen ? 'ring-2 ring-blue-500/20' : ''}
                    ${selectedValue
                    ? 'bg-white dark:bg-zinc-900 text-gray-900 dark:text-zinc-100 border-gray-300 dark:border-zinc-700'
                    : 'bg-gray-50 dark:bg-zinc-800 text-gray-500 dark:text-zinc-400 border-gray-200 dark:border-zinc-600'
                }
                `}
                aria-haspopup="listbox"
                aria-expanded={isOpen}
            >
                <span className="truncate">
                    {selectedValue || placeholder}
                </span>
                <ChevronDown
                    size={16}
                    className={`transition-transform duration-200 ${isOpen ? 'rotate-180' : ''}`}
                />
            </button>

            {/* Выпадающий список */}
            {isOpen && (
                <div className="absolute z-50 w-full mt-1 rounded-xl border border-gray-200 dark:border-zinc-700 bg-white dark:bg-zinc-900 shadow-lg overflow-hidden">
                    <ul
                        role="listbox"
                        className="py-1 max-h-60 overflow-auto"
                    >
                        {items.map((item, index) => (
                            <li key={index}>
                                <button
                                    type="button"
                                    onClick={() => handleSelect(item)}
                                    className={`
                                        w-full px-4 py-2.5 text-left
                                        transition-colors duration-150
                                        flex items-center justify-between
                                        ${selectedValue === item
                                        ? 'bg-blue-50 dark:bg-blue-900/20 text-blue-600 dark:text-blue-400 font-medium'
                                        : 'text-gray-700 dark:text-zinc-300 hover:bg-gray-50 dark:hover:bg-zinc-800'
                                    }
                                    `}
                                    role="option"
                                    aria-selected={selectedValue === item}
                                >
                                    <span>{item}</span>
                                    {selectedValue === item && (
                                        <div className="w-1.5 h-1.5 rounded-full bg-blue-500 dark:bg-blue-400" />
                                    )}
                                </button>
                            </li>
                        ))}
                    </ul>
                </div>
            )}
        </div>
    )
}