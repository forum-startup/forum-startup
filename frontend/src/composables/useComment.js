import { ref } from 'vue'
import api from '../utils/axios.js'
import { useCommentValidation } from './useCommentValidation'
import { currentUser } from '../utils/store.js'
import {toast} from "vue3-toastify";

export function useComment() {
    const form = ref({
        content: '',
        postId: null,
        parentId: null  // null = top-level comment, number = reply
    })

    const isLoading = ref(false)
    const error = ref(null)

    const { errors, validate, clearErrors } = useCommentValidation(form)

    async function createComment() {
        clearErrors()

        if (!validate()) return false

        if (!form.value.postId) {
            error.value = 'Post ID is missing'
            return false
        }

        isLoading.value = true
        error.value = null

        try {
            const payload = {
                content: form.value.content.trim(),
            }

            // Only send parentId if it's a reply
            if (form.value.parentId) {
                payload.parentId = form.value.parentId
            }

            const res = await api.post(`/private/posts/${form.value.postId}/comments`, payload)

            toast.success("You posted a comment!", {
                autoClose: 3000,
                position: toast.POSITION.TOP_RIGHT,
                theme: "dark",
            })

            form.value.content = ''
            form.value.parentId = null

            return res.data
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to post comment'
            return false
        } finally {
            isLoading.value = false
        }
    }

    async function editComment(commentId) {
        clearErrors()

        if (!validate()) return false

        if (!form.value.postId) {
            error.value = 'Post ID is missing'
            return false
        }

        isLoading.value = true
        error.value = null

        isLoading.value = true
        try {
            const payload = {
                content: form.value.content.trim(),
            }

            const res = await api.put(`/private/comments/${commentId}`, payload)

            toast.success("Comment edited!", {
                autoClose: 3000,
                position: toast.POSITION.TOP_RIGHT,
                theme: "dark",
            })

            form.value.content = ''
            form.value.parentId = null

            return res.data
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to edit comment'
            return false
        } finally {
            isLoading.value = false
        }
    }

    async function deleteComment(commentId) {
        isLoading.value = true
        try {
            await api.delete(`/private/comments/${commentId}`)

            toast.success("Comment deleted!", {
                autoClose: 3000,
                position: toast.POSITION.TOP_RIGHT,
                theme: "dark",
            })

            return true
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to delete'
            return false
        } finally {
            isLoading.value = false
        }
    }

    async function adminDeleteComment(commentId) {
        isLoading.value = true
        try {
            await api.delete(`/admin/comments/${commentId}`)

            toast.success("Comment deleted! Keep the community safe!", {
                autoClose: 3000,
                position: toast.POSITION.TOP_RIGHT,
                theme: "dark",
            })

            return true
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to delete'
            return false
        } finally {
            isLoading.value = false
        }
    }

    // Helpers
    function startReply(postId, parentId) {
        form.value.postId = postId
        form.value.parentId = parentId
        form.value.content = ''
    }

    function cancelReply() {
        form.value.parentId = null
        form.value.content = ''
    }

    const isOwnComment = (comment) => {
        return currentUser.value && comment.creatorId === currentUser.value.id
    }

    const canDelete = (comment) => {
        if (!currentUser.value) return false
        const isOwner = isOwnComment(comment)
        const isAdmin = currentUser.value.roles?.some(r => r.name === 'ROLE_ADMIN')
        return isOwner || isAdmin
    }

    return {
        form,
        isLoading,
        error,
        errors,
        createComment,
        editComment,
        deleteComment,
        adminDeleteComment,
        startReply,
        cancelReply,
        canDelete,
    }
}