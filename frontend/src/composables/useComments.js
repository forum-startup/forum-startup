import {ref} from "vue";
import {currentUser} from "../utils/store.js";
import api from "../utils/axios.js";

export function useComments() {
    const comments = ref([])
    const isLoading = ref(false)
    const errors = ref(null)

    async function fetchCommentsByPostId(
        postId,
        page = 0,
        size = 10,
        sort = undefined,
    ) {
        if (!currentUser.value?.id) {
            errors.value = 'You must be logged in'
            return
        }

        isLoading.value = true
        errors.value = null

        try {
            const params = Object.fromEntries(
                Object.entries({ page, size, sort })
                    .filter(([_, v]) => v !== undefined)
            );

            const res = await api.get(
                `/private/posts/${postId}/comments`,
                {params}
            )
            comments.value = res.data.content
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

    function updateComment(updatedComment) {
        const index = comments.value.findIndex(c => c.id === updatedComment.id)
        if (index !== -1) {
            comments.value[index] = updatedComment
            comments.value = [...comments.value]
        }
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
        updateComment,
        removeComment,
    }
}