<script setup>
import { ref, computed } from 'vue'
import { format } from 'date-fns'
import { useComment } from '../composables/useComment'

const props = defineProps({
  comment: Object,
  allComments: Array,
  depth: Number,
  postId: [String, Number] // pass postId from parent
})

const replies = computed(() =>
    props.allComments.filter(c => c.parentId === props.comment.id)
)

const showReplyForm = ref(false)
const { form, isLoading, errors, error, createComment, startReply, cancelReply } = useComment()

// Start replying
function reply() {
  showReplyForm.value = true
  startReply(props.postId, props.comment.id)
}

// Submit reply
async function submitReply() {
  const success = await createComment()
  if (success) {
    showReplyForm.value = false
    // Optionally: refetch comments or push new comment into list
  }
}
</script>

<template>
  <div class="flex gap-5" :class="{ 'ml-0': depth === 0, 'ml-12': depth > 0 }">
    <!-- Avatar -->
    <div class="flex-shrink-0">
      <div class="w-11 h-11 rounded-full bg-gradient-to-br from-cyan-500 to-blue-600 flex items-center justify-center text-white font-bold text-lg">
        {{ comment.creatorUsername[0].toUpperCase() }}
      </div>
    </div>

    <!-- Comment Body -->
    <div class="flex-1">
      <div class="bg-white/5 backdrop-blur-sm rounded-2xl px-6 py-5 border border-white/10">
        <div class="flex items-center gap-3 mb-2">
          <strong class="text-white">@{{ comment.creatorUsername }}</strong>
          <span class="text-xs text-gray-400">
            {{ format(new Date(comment.createdAt), 'MMM d, yyyy • h:mm a') }}
          </span>
        </div>

        <p class="text-gray-100 leading-relaxed">{{ comment.content }}</p>

        <!-- Actions -->
        <div class="flex items-center gap-6 mt-4 text-sm">
          <button @click="reply" class="text-indigo-400 hover:text-indigo-300 font-medium">
            Reply
          </button>
          <button class="flex items-center gap-1 text-gray-400 hover:text-red-400">
            Heart {{ comment.likesCount || 0 }}
          </button>
        </div>

        <!-- Reply Form -->
        <div v-if="showReplyForm" class="mt-6">
          <textarea
              v-model="form.content"
              rows="3"
              placeholder="Write a reply..."
              class="w-full rounded-xl bg-white/10 px-4 py-3 text-white placeholder-gray-500 focus:ring-2 focus:ring-indigo-500 focus:outline-none resize-none"
          ></textarea>
          <p v-if="errors.content" class="text-red-400 text-sm mt-1">{{ errors.content }}</p>
          <p v-if="error" class="text-red-400 text-sm mt-1">{{ error }}</p>

          <div class="flex gap-3 mt-3">
            <button
                @click="submitReply"
                :disabled="isLoading"
                class="px-5 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-500 disabled:opacity-50 text-sm font-medium"
            >
              {{ isLoading ? 'Posting...' : 'Post Reply' }}
            </button>
            <button @click="() => { showReplyForm = false; cancelReply() }" class="text-gray-400 hover:text-white text-sm">
              Cancel
            </button>
          </div>
        </div>
      </div>

      <!-- Nested Replies -->
      <div v-if="replies.length" class="mt-8 space-y-8">
        <CommentItem
            v-for="reply in replies"
            :key="reply.id"
            :comment="reply"
            :all-comments="allComments"
            :post-id="postId"
            :depth="depth + 1"
        />
      </div>
    </div>
  </div>
</template>