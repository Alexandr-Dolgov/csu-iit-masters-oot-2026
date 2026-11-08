<!--
  Слой поверх каждого слайда (соглашение Slidev: slide-top.vue в папке колоды).

  - Номер слайда — справа внизу, только в колодах с `slideNumbers: true`
    в headmatter (колоды v1/v2 его не ставят и остаются без номеров).
  - Хэш коммита — слева внизу, если во frontmatter слайда есть `commit: <hash>`.
    Так конспект связывает слайд с состоянием демо-кода: `git checkout <hash>`.
    Хэш — ссылка на коммит на GitHub.
-->
<template>
  <div v-if="$frontmatter.commit" class="commit-tag">
    <span class="commit-tag-label">git checkout</span>
    <a
      :href="'https://github.com/Alexandr-Dolgov/csu-iit-masters-oot-2026/commit/' + $frontmatter.commit"
      target="_blank"
      rel="noopener"
    ><code>{{ $frontmatter.commit }}</code></a>
  </div>
  <div v-if="$slidev.configs.slideNumbers" class="slide-no">
    {{ $page }} / {{ $nav.total }}
  </div>
</template>
