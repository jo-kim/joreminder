export const COLOR_MAP: Record<string, string> = {
  RED: "var(--color-red)",
  ORANGE: "var(--color-orange)",
  YELLOW: "var(--color-yellow)",
  GREEN: "var(--color-green)",
  CYAN: "var(--color-cyan)",
  BLUE: "var(--color-blue)",
  INDIGO: "var(--color-indigo)",
  PURPLE: "var(--color-purple)",
  PINK: "var(--color-pink)",
  BROWN: "var(--color-brown)",
  GRAY: "var(--color-gray)",
  TEAL: "var(--color-teal)",
};

export function getColor(color: string): string {
  return COLOR_MAP[color] ?? COLOR_MAP.BLUE;
}
