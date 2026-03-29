import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "JoReminder",
  description: "Apple Reminders Web Clone",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko">
      <body>{children}</body>
    </html>
  );
}
