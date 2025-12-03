import {ref} from "vue";
import {currentUser} from "../utils/store.js";
import api from "../utils/axios.js";

export function useComments() {
    const comments = ref([])
    const isLoading = ref(false)
    const errors = ref(null)

    async function fetchCommentsByPostId(postId) {
        if (!currentUser.value?.id) {
            errors.value = 'You must be logged in'
            return
        }

        isLoading.value = true
        errors.value = null

        try {
            const res = await api.get(`/private/posts/${postId}/comments`)
            comments.value = res.data
        } catch (err) {
            errors.value = err.response?.data?.message || 'Failed to load comments'
        } finally {
            isLoading.value = false
        }
    }

    function addComment(newComment) {
        comments.value.push(newComment)
        comments.value.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
    }

    function removeComment(commentId) {
        comments.value = comments.value.filter(c => c.id !== commentId)
    }

    return {
        comments,
        isLoading,
        errors,
        fetchCommentsByPostId,
        addComment,
        removeComment,
    }
}