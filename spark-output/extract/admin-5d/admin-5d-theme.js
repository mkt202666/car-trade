// React Theme — extracted from http://shengtaiprd.pancosky.com/5dadmin/
// Compatible with: Chakra UI, Stitches, Vanilla Extract, or any CSS-in-JS

/**
 * TypeScript type definition for this theme:
 *
 * interface Theme {
 *   colors: {
    primary: string;
    secondary: string;
    accent: string;
    background: string;
    foreground: string;
    neutral50: string;
    neutral100: string;
    neutral200: string;
    neutral300: string;
    neutral400: string;
    neutral500: string;
 *   };
 *   fonts: {
    body: string;
 *   };
 *   fontSizes: {
    '9': string;
    '10': string;
    '11': string;
    '12': string;
    '14': string;
    '16': string;
    '18': string;
    '30': string;
    '10.5': string;
 *   };
 *   space: {
    '2': string;
    '16': string;
    '20': string;
    '24': string;
    '40': string;
 *   };
 *   radii: {
    sm: string;
    md: string;
    lg: string;
 *   };
 *   shadows: {
    sm: string;
 *   };
 *   states: {
 *     hover: { opacity: number };
 *     focus: { opacity: number };
 *     active: { opacity: number };
 *     disabled: { opacity: number };
 *   };
 * }
 */

export const theme = {
  "colors": {
    "primary": "#e4e7eb",
    "secondary": "#c12a00",
    "accent": "#dae2ff",
    "background": "#f0f4f7",
    "foreground": "#000000",
    "neutral50": "#03060a",
    "neutral100": "#515b6d",
    "neutral200": "#252b39",
    "neutral300": "#ffffff",
    "neutral400": "#f2f4f7",
    "neutral500": "#a8a8b0"
  },
  "fonts": {
    "body": "'Inter', sans-serif"
  },
  "fontSizes": {
    "9": "9px",
    "10": "10px",
    "11": "11px",
    "12": "12px",
    "14": "14px",
    "16": "16px",
    "18": "18px",
    "30": "30px",
    "10.5": "10.5px"
  },
  "space": {
    "2": "2px",
    "16": "16px",
    "20": "20px",
    "24": "24px",
    "40": "40px"
  },
  "radii": {
    "sm": "4px",
    "md": "8px",
    "lg": "16px"
  },
  "shadows": {
    "sm": "rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0.1) 0px 1px 3px 0px, rgba(0, 0, 0, 0.1) 0px 1px 2px -1px"
  },
  "states": {
    "hover": {
      "opacity": 0.08
    },
    "focus": {
      "opacity": 0.12
    },
    "active": {
      "opacity": 0.16
    },
    "disabled": {
      "opacity": 0.38
    }
  }
};

// MUI v5 theme
export const muiTheme = {
  "palette": {
    "primary": {
      "main": "#e4e7eb",
      "light": "hsl(214, 15%, 95%)",
      "dark": "hsl(214, 15%, 76%)"
    },
    "secondary": {
      "main": "#c12a00",
      "light": "hsl(13, 100%, 53%)",
      "dark": "hsl(13, 100%, 23%)"
    },
    "background": {
      "default": "#f0f4f7",
      "paper": "#ffffff"
    },
    "text": {
      "primary": "#000000",
      "secondary": "#010205"
    }
  },
  "typography": {
    "h2": {
      "fontSize": "30px",
      "fontWeight": "700",
      "lineHeight": "36px"
    },
    "body1": {
      "fontSize": "16px",
      "fontWeight": "400",
      "lineHeight": "24px"
    },
    "body2": {
      "fontSize": "11px",
      "fontWeight": "400",
      "lineHeight": "16.5px"
    }
  },
  "shape": {
    "borderRadius": 8
  },
  "shadows": [
    "rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0.1) 0px 4px 6px -1px, rgba(0, 0, 0, 0.1) 0px 2px 4px -2px",
    "rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0.05) 0px 2px 4px 0px inset",
    "rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0.1) 0px 1px 3px 0px, rgba(0, 0, 0, 0.1) 0px 1px 2px -1px"
  ]
};

export default theme;
