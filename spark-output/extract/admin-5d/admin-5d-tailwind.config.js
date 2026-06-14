/** @type {import('tailwindcss').Config} */
export default {
  theme: {
    extend: {
    colors: {
        primary: {
            '50': 'hsl(214, 15%, 97%)',
            '100': 'hsl(214, 15%, 94%)',
            '200': 'hsl(214, 15%, 86%)',
            '300': 'hsl(214, 15%, 76%)',
            '400': 'hsl(214, 15%, 64%)',
            '500': 'hsl(214, 15%, 50%)',
            '600': 'hsl(214, 15%, 40%)',
            '700': 'hsl(214, 15%, 32%)',
            '800': 'hsl(214, 15%, 24%)',
            '900': 'hsl(214, 15%, 16%)',
            '950': 'hsl(214, 15%, 10%)',
            DEFAULT: '#e4e7eb'
        },
        secondary: {
            '50': 'hsl(13, 100%, 97%)',
            '100': 'hsl(13, 100%, 94%)',
            '200': 'hsl(13, 100%, 86%)',
            '300': 'hsl(13, 100%, 76%)',
            '400': 'hsl(13, 100%, 64%)',
            '500': 'hsl(13, 100%, 50%)',
            '600': 'hsl(13, 100%, 40%)',
            '700': 'hsl(13, 100%, 32%)',
            '800': 'hsl(13, 100%, 24%)',
            '900': 'hsl(13, 100%, 16%)',
            '950': 'hsl(13, 100%, 10%)',
            DEFAULT: '#c12a00'
        },
        accent: {
            '50': 'hsl(227, 100%, 97%)',
            '100': 'hsl(227, 100%, 94%)',
            '200': 'hsl(227, 100%, 86%)',
            '300': 'hsl(227, 100%, 76%)',
            '400': 'hsl(227, 100%, 64%)',
            '500': 'hsl(227, 100%, 50%)',
            '600': 'hsl(227, 100%, 40%)',
            '700': 'hsl(227, 100%, 32%)',
            '800': 'hsl(227, 100%, 24%)',
            '900': 'hsl(227, 100%, 16%)',
            '950': 'hsl(227, 100%, 10%)',
            DEFAULT: '#dae2ff'
        },
        'neutral-50': '#03060a',
        'neutral-100': '#515b6d',
        'neutral-200': '#252b39',
        'neutral-300': '#ffffff',
        'neutral-400': '#f2f4f7',
        'neutral-500': '#a8a8b0',
        background: '#f0f4f7',
        foreground: '#000000'
    },
    fontFamily: {
        sans: [
            'Inter',
            'sans-serif'
        ]
    },
    fontSize: {
        '9': [
            '9px',
            {
                lineHeight: '11.25px'
            }
        ],
        '10': [
            '10px',
            {
                lineHeight: '15px'
            }
        ],
        '11': [
            '11px',
            {
                lineHeight: '16.5px'
            }
        ],
        '12': [
            '12px',
            {
                lineHeight: '16px'
            }
        ],
        '14': [
            '14px',
            {
                lineHeight: '20px'
            }
        ],
        '16': [
            '16px',
            {
                lineHeight: '24px'
            }
        ],
        '18': [
            '18px',
            {
                lineHeight: '28px',
                letterSpacing: '0.9px'
            }
        ],
        '30': [
            '30px',
            {
                lineHeight: '36px',
                letterSpacing: '-0.75px'
            }
        ],
        '10.5': [
            '10.5px',
            {
                lineHeight: '10.5px'
            }
        ]
    },
    spacing: {
        '1': '2px',
        '8': '16px',
        '10': '20px',
        '12': '24px',
        '20': '40px'
    },
    borderRadius: {
        sm: '4px',
        md: '8px',
        lg: '16px'
    },
    boxShadow: {
        sm: 'rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0) 0px 0px 0px 0px, rgba(0, 0, 0, 0.1) 0px 1px 3px 0px, rgba(0, 0, 0, 0.1) 0px 1px 2px -1px'
    },
    transitionDuration: {
        '150': '0.15s',
        '200': '0.2s',
        '500': '0.5s'
    },
    transitionTimingFunction: {
        custom: 'cubic-bezier(0.4, 0, 0.2, 1)'
    }
},
  },
};
