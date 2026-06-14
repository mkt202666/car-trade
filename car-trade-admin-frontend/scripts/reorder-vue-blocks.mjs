import { readFileSync, writeFileSync, readdirSync, statSync } from 'node:fs'
import { join, extname } from 'node:path'
import { parse } from '@vue/compiler-sfc'

const root = new URL('..', import.meta.url).pathname

function walkVueFiles(dir) {
  const files = []
  for (const entry of readdirSync(dir)) {
    const fullPath = join(dir, entry)
    if (statSync(fullPath).isDirectory()) {
      files.push(...walkVueFiles(fullPath))
    } else if (extname(fullPath) === '.vue') {
      files.push(fullPath)
    }
  }
  return files
}

function block(tag, attrs, content) {
  const attrStr = attrs.length ? ` ${attrs.join(' ')}` : ''
  return `<${tag}${attrStr}>\n${content}\n</${tag}>`
}

function reorderVueFile(content) {
  const { descriptor } = parse(content, { pad: false })
  const parts = []

  if (descriptor.template) {
    parts.push(block('template', [], descriptor.template.content))
  }

  if (descriptor.scriptSetup) {
    const attrs = ['setup']
    if (descriptor.scriptSetup.lang) attrs.push(`lang="${descriptor.scriptSetup.lang}"`)
    parts.push(block('script', attrs, descriptor.scriptSetup.content))
  } else if (descriptor.script) {
    const attrs = []
    if (descriptor.script.setup) attrs.push('setup')
    if (descriptor.script.lang) attrs.push(`lang="${descriptor.script.lang}"`)
    parts.push(block('script', attrs, descriptor.script.content))
  }

  for (const style of descriptor.styles) {
    const attrs = ['lang="scss"']
    if (style.scoped) attrs.push('scoped')
    if (style.module) attrs.push('module')
    parts.push(block('style', attrs, style.content))
  }

  return `${parts.join('\n\n')}\n`
}

const files = walkVueFiles(join(root, 'src'))

for (const file of files) {
  const content = readFileSync(file, 'utf8')
  writeFileSync(file, reorderVueFile(content), 'utf8')
  console.log(`reordered: ${file}`)
}
