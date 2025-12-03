<script setup>
import { ref, computed } from 'vue'
import { format } from 'date-fns'
import CommentForm from './CommentForm.vue'
import { useCommentLogic } from '../composables/useCommentLogic'

const props = defineProps({
  comment: { type: Object, required: true },
  allComments: { type: Array, required: true },
  depth: { type: Number, default: 0 },
  postId: { type: [String, Number], required: true }
})

const emit = defineEmits(['comment-updated', 'comment-deleted'])

const replies = computed(() =>
    props.allComments.filter(c => c.parentId === props.comment.id)
)

const showReplyForm = ref(false)
const showMenu = ref(false)

const {
  isEditing,
  canEdit,
  canDelete,
  startEdit,
  cancelEdit,
  submitEdit,
  handleDelete,
  isLoading,
  errors
} = useCommentLogic(() => props.comment, () => props.allComments, props.postId)

// Dynamic indentation
const indentClass = computed(() => {
  const indent = Math.min(props.depth * 12, 60)
  return `ml-${indent}`
})

function onReplySuccess(newComment) {
  showReplyForm.value = false
  emit('comment-updated', newComment)
}
</script>

<template>
  <div class="flex gap-5" :class="indentClass">
    <!-- Avatar -->
    <div class="flex-shrink-0">
      <div class="w-11 h-11 rounded-full bg-gradient-to-br from-cyan-500 to-blue-600 flex items-center justify-center text-white font-bold text-lg">
        {{ comment.creatorUsername[0].toUpperCase() }}
      </div>
    </div>

    <!-- Comment Body -->
    <div class="flex-1">
      <div class="bg-white/5 backdrop-blur-sm rounded-2xl px-6 py-5 border border-white/10">
        <!-- Header -->
        <div class="flex items-center justify-between mb-2">
          <div class="flex items-center gap-3">
            <strong class="text-white">@{{ comment.creatorUsername }}</strong>
            <span class="text-xs text-gray-400">
              {{ format(new Date(comment.createdAt), 'MMM d, yyyy • h:mm a') }}
            </span>
            <span v-if="comment.deleted" class="text-xs text-gray-500 italic">
              [deleted<template v-if="comment.deletedByUsername"> by @{{ comment.deletedByUsername }}</template>]
            </span>
          </div>

          <!-- More menu -->
          <div v-if="canEdit || canDelete" class="relative">
            <button @click.stop="showMenu = !showMenu" class="p-2 rounded-lg hover:bg-white/10 transition">
              <svg class="w-5 h-5 text-gray-400" fill="currentColor" viewBox="0 0 20 20">
                <path d="M10 6a2 2 0 110-4 2 2 0 010 4zM10 12a2 2 0 110-4 2 2 0 010 4zM10 18a2 2 0 110-4 2 2 0 010 4z"/>
              </svg>
            </button>

            <div v-if="showMenu" @click.stop class="absolute right-0 mt-2 w-48 bg-gray-800 rounded-lg shadow-2xl border border-gray-700 py-2 z-50">
              <button v-if="canEdit" @click="startEdit(); showMenu = false" class="w-full text-left px-5 py-3 text-sm text-gray-300 hover:bg-gray-700">
                Edit
              </button>
              <button @click="handleDelete().then(success => success && (showMenu = false) && $emit('comment-deleted', comment.id))" class="w-full text-left px-5 py-3 text-sm text-red-400 hover:bg-red-900/20">
                Delete
              </button>
            </div>
          </div>
        </div>

        <!-- Content -->
        <div v-if="comment.deleted" class="text-gray-500 italic">
          [comment deleted]
        </div>

        <!-- Edit Form -->
        <div v-else-if="isEditing" class="mt-4">
          <textarea
              v-model="form.content"
              rows="3"
              placeholder="Edit your comment..."
              class="w-full rounded-xl bg-white/10 px-4 py-3 text-white placeholder-gray-500 focus:ring-2 focus:ring-indigo-500 outline-none resize-none border border-white/10"
          />
          <p v-if="errors.content" class="text-red-400 text-sm mt-1">{{ errors.content }}</p>

          <div class="flex gap-3 mt-3">
            <button @click="submitEdit().then(updated => updated && $emit('comment-updated', updated))" :disabled="isLoading" class="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-500 disabled:opacity-50 text-sm">
              {{ isLoading ? 'Saving...' : 'Save' }}
            </button>
            <button @click="cancelEdit" class="text-gray-400 hover:text-white text-sm">Cancel</button>
          </div>
        </div>

        <!-- Normal Content -->
        <p v-else class="text-gray-100 leading-relaxed">
          {{ comment.content }}
        </p>

        <!-- Actions -->
        <div class="flex items-center gap-6 mt-4 text-sm">
          <button @click="showReplyForm = true" class="text-indigo-400 hover:text-indigo-300 font-medium transition">
            Reply
          </button>
          <button class="flex items-center gap-1 text-gray-400 hover:text-red-400">
            Heart {{ comment.likesCount || 0 }}
          </button>
        </div>

        <!-- Reply Form -->
        <transition name="fade">
          <div v-if="showReplyForm" class="mt-6 -mx-6 px-6 pt-4 bg-white/5 rounded-b-2xl border-t border-white/10">
            <CommentForm
                :post-id="postId"
                :parent-id="comment.id"
                auto-focus
                @commented="onReplySuccess"
                @cancel="showReplyForm = false"
            />
          </div>
        </transition>
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
            @comment-updated="$emit('comment-updated', $event)"
            @comment-deleted="$emit('comment-deleted', $event)"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>