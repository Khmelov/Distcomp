"use client"

import {useState, useEffect, useCallback, useMemo} from 'react'
import {API_URL, IRequestItem} from '@/app/resources/data'
import { getBcBlockColor, getBcColor } from '@/app/resources/utils'
import { X, Copy, Check, Send } from 'lucide-react'
import {sendRequest} from "@/app/entities/request";

interface RequestModalProps {
    isOpen: boolean;
    requestItem?: IRequestItem;
    onClose: () => void;
    apiVersion: string;
}

export const RequestModal = ({
                                 isOpen,
                                 requestItem,
                                 onClose,
                                 apiVersion
                             }: RequestModalProps) => {
    const [copied, setCopied] = useState(false)
    const [loading, setLoading] = useState(false)
    const [response, setResponse] = useState<string>('')
    const [requestBody, setRequestBody] = useState('{\n  "example": "data"\n}')
    const [resultUrl, setResultUrl] = useState<string>(requestItem?.url || '')
    const FULL_API_URL = useMemo(() =>
        `${API_URL}${apiVersion}${resultUrl}`,
    [apiVersion, resultUrl]);

    useEffect(() => {
        const handleEscape = (e: KeyboardEvent) => {
            if (e.key === 'Escape') onClose()
        }
        if (isOpen) {
            document.addEventListener('keydown', handleEscape)
            document.body.style.overflow = 'hidden'
        }
        return () => {
            document.removeEventListener('keydown', handleEscape)
            document.body.style.overflow = 'unset'
        }
    }, [isOpen, onClose])

    const handleCopy = useCallback(() => {
        if (!requestItem) return
        const text = `${requestItem.method} ${requestItem.url}`
        navigator.clipboard.writeText(text)
        setCopied(true)
        setTimeout(() => setCopied(false), 2000)
    }, [requestItem])

    console.log(FULL_API_URL);

    const handleSendRequest = async () => {
        if (!requestItem) return

        setLoading(true)

        try {
            let body;
            if (['POST', 'PUT', 'PATCH'].includes(requestItem.method) && requestBody) {
                try {
                    body = JSON.parse(requestBody)
                } catch (e) {
                    setResponse(`❌ Invalid JSON in request body:\n${e}`)
                    setLoading(false)
                    return
                }
            }

            const result = await sendRequest(
                requestItem.method,
                FULL_API_URL,
                {
                    body,
                    headers: {
                        'Authorization': 'Bearer your-token',
                    }
                }
            )

            if (result.success) {
                setResponse(JSON.stringify(result.data, null, 2))
            } else {
                setResponse(`❌ Error: ${result.error}`)
            }
        } catch (error) {
            setResponse(`❌ Request failed:\n${error}`)
        } finally {
            setLoading(false)
        }
    }

    if (!isOpen || !requestItem) return null

    return (
        <>
            <div
                className="fixed inset-0 bg-black/50 dark:bg-black/70 backdrop-blur-sm z-40"
                onClick={onClose}
            />

            <div className="fixed inset-0 flex items-center justify-center p-4 z-50">
                <div
                    className={`
                        w-full max-w-2xl rounded-2xl shadow-2xl
                        bg-white dark:bg-zinc-900
                        border dark:border-zinc-800
                        overflow-hidden
                        transform transition-all duration-300
                        ${isOpen ? 'scale-100 opacity-100' : 'scale-95 opacity-0'}
                    `}
                    onClick={e => e.stopPropagation()}
                >
                    <div className={`
                        flex items-center justify-between p-6
                        border-b dark:border-zinc-800
                        ${getBcBlockColor(requestItem.method)}
                    `}>
                        <div className="flex items-center gap-3">
                            <div className={`
                                px-3 py-1 rounded-full text-sm font-medium
                                ${getBcColor(requestItem.method)}
                            `}>
                                {requestItem.method}
                            </div>
                            <h2 className="text-xl font-semibold text-black dark:text-zinc-50">
                                {requestItem.url}
                            </h2>
                        </div>
                        <button
                            onClick={onClose}
                            className="p-2 rounded-lg hover:bg-gray-100 dark:hover:bg-zinc-800
                                     text-gray-500 dark:text-zinc-400
                                     transition-colors"
                        >
                            <X size={20} />
                        </button>
                    </div>

                    <div className="p-6 space-y-6">
                        <div>
                            <label className="block text-sm font-medium text-gray-700 dark:text-zinc-300 mb-2">
                                Endpoint URL
                            </label>
                            <div className="flex gap-2">
                                <input
                                    type="text"
                                    value={resultUrl}
                                    onChange={(e) => { setResultUrl(e.target.value)}}
                                    className="flex-1 px-4 py-2 rounded-lg bg-gray-50 dark:bg-zinc-800
                                             border dark:border-zinc-700 text-gray-900 dark:text-zinc-100"
                                />
                                <button
                                    onClick={handleCopy}
                                    className="px-4 py-2 rounded-lg bg-gray-100 dark:bg-zinc-800
                                             hover:bg-gray-200 dark:hover:bg-zinc-700
                                             transition-colors flex items-center gap-2"
                                >
                                    {copied ? <Check size={16} /> : <Copy size={16} />}
                                    {copied ? 'Copied!' : 'Copy'}
                                </button>
                            </div>
                        </div>

                        {(requestItem.method === 'POST' || requestItem.method === 'PUT') && (
                            <div>
                                <label className="block text-sm font-medium text-gray-700 dark:text-zinc-300 mb-2">
                                    Request Body
                                </label>
                                <textarea
                                    value={requestBody}
                                    onChange={(e) => setRequestBody(e.target.value)}
                                    className="w-full h-32 px-4 py-3 rounded-lg bg-gray-50 dark:bg-zinc-800
                                             border dark:border-zinc-700 text-gray-900 dark:text-zinc-100
                                             font-mono text-sm resize-none"
                                    spellCheck={false}
                                />
                            </div>
                        )}

                        <div>
                            <div className="flex items-center justify-between mb-2">
                                <label className="text-sm font-medium text-gray-700 dark:text-zinc-300">
                                    Response
                                </label>
                                <button
                                    onClick={handleSendRequest}
                                    disabled={loading}
                                    className={`px-4 py-2 rounded-lg flex items-center gap-2
                                               ${getBcColor(requestItem.method)}
                                               ${loading ? 'opacity-50 cursor-not-allowed' : ''}
                                               transition-all cursor-pointer`}
                                >
                                    {loading ? (
                                        <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-current" />
                                    ) : (
                                        <Send size={16} />
                                    )}
                                    Send Request
                                </button>
                            </div>
                            <pre className="w-full h-48 px-4 py-3 rounded-lg bg-gray-50 dark:bg-zinc-800
                                         border dark:border-zinc-700 text-gray-900 dark:text-zinc-100
                                         font-mono text-sm overflow-auto whitespace-pre-wrap">
                                {response || 'Click "Send Request" to see response...'}
                            </pre>
                        </div>
                    </div>

                    {/* Footer */}
                    <div className="px-6 py-4 border-t dark:border-zinc-800 bg-gray-50 dark:bg-zinc-900/50">
                        <div className="flex justify-end gap-3">
                            <button
                                onClick={onClose}
                                className="px-4 py-2 rounded-lg border dark:border-zinc-700
                                         text-gray-700 dark:text-zinc-300
                                         hover:bg-gray-100 dark:hover:bg-zinc-800
                                         transition-colors cursor-pointer"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={handleSendRequest}
                                disabled={loading}
                                className={`px-4 py-2 rounded-lg text-white dark:text-zinc-50
                                         flex items-center gap-2
                                         ${getBcColor(requestItem.method)}
                                         ${loading ? 'opacity-50 cursor-not-allowed' : ''}
                                         transition-all cursor-pointer`}
                            >
                                {loading ? 'Sending...' : 'Send Request'}
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </>
    )
}