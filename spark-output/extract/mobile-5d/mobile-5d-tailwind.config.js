/** @type {import('tailwindcss').Config} */
export default {
  theme: {
    extend: {
    colors: {
        'neutral-50': '#000000',
        foreground: '#000000'
    },
    fontFamily: {
        body: [
            'Times New Roman',
            'sans-serif'
        ]
    },
    fontSize: {
        '16': [
            '16px',
            {
                lineHeight: 'normal'
            }
        ]
    },
    spacing: {
        '0': '8px'
    }
},
  },
};
