"use client"
import {RequestBlock} from "@/app/Request/RequestBlock";
import {API_VERSIONS, REQUESTS} from "@/app/resources/data";
import {Select} from "@/app/Input/Select";
import {useState} from "react";

export default function Home() {
  const [selectedVersion, setSelectedVersion] = useState<string>(API_VERSIONS[0])

  return (
    <div className="flex gap-4 flex-col min-h-screen p-10 bg-zinc-50 font-sans dark:bg-black">
      <div className="flex flex-row justify-between">
        <h1 className="max-w-xs text-3xl font-semibold leading-10 tracking-tight text-black dark:text-zinc-50">
            My custom API
        </h1>

          <Select
              items={API_VERSIONS}
              value={selectedVersion}
              onChange={setSelectedVersion}
          />
      </div>

      <div className="flex align-middle flex-col gap-10">
          {REQUESTS.map((item, index) => (
              <RequestBlock
                  key={index}
                  header={item.header}
                  items={item.items}
                  apiVersion={selectedVersion}
              />
          ))}
      </div>
    </div>
  );
}
