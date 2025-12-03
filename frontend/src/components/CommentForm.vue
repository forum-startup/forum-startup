<script setup>
import { watchEffect } from 'vue'
import { useComment } from '../composables/useComment'

const props = defineProps({
  postId: { type: [String, Number], required: true },
  parentId: { type: [String, Number], default: null },
  autoFocus: { type: Boolean, default: false }
})

const emit = defineEmits(['commented', 'cancel'])

const { form, isLoading, errors, error, createComment } = useComment()

// Sync props → form
watchEffect(() => {
  form.value.postId = props.postId
  form.value.parentId = props.parentId || null
})

async function submit() {
  if (!form.value.content.trim()) return

  const newComment = await createComment()
  if (newComment) {
    form.value.content = ''
    form.value.parentId = null
    emit('commented', newComment)
  }
}
</script>

<template>
  <div>
    <textarea
        v-model="form.content"
        :placeholder="parentId ? 'Write a reply...' : 'Share your thoughts...'"
        rows="4"
        class="w-full rounded-xl bg-white/10 px-5 py-4 text-white placeholder-gray-500 focus:ring-2 focus:ring-indigo-500 outline-none resize-none border border-white/10"
    />

    <div v-if="errors.content || error" class="mt-2 text-red-400 text-sm">
      {{ errors.content || error }}
    </div>

    <div class="mt-4 flex justify-between items-center">
      <button
          @click="submit"
          :disabled="isLoading || !form.content.trim()"
          class="px-6 py-3 bg-indigo-600 text-white rounded-xl hover:bg-indigo-500 disabled:opacity-50 font-medium transition"
      >
        {{ isLoading ? 'Posting...' : parentId ? 'Post Reply' : 'Post Comment' }}
      </button>

      <button
          v-if="parentId"
          @click="$emit('cancel')"
          class="text-gray-400 hover:text-white text-sm font-medium"
      >
        Cancel
      </button>
    </div>
  </div>
</template>