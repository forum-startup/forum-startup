import {ref} from "vue";

export function useCommentValidation(comment) {
    const errors = ref({})

    const rules = {
        content: (v) => {
            const len = v?.trim().length || 0
            if (!v?.trim()) return 'Content is required'
            if (len < 1) return 'Comment must be at least 1 character'
            if (len > 1000) return 'Comment cannot exceed 1000 characters'
            return true
        },
    }

    function validate() {
        errors.value = {}
        let isValid = true

        Object.keys(rules).forEach((key) => {
            const value = comment.value[key]
            const result = rules[key](value)
            if (result !== true) {
                errors.value[key] = result
                isValid = false
            }
        })

        return isValid
    }

    function clearErrors() {
        errors.value = {}
    }

    return {
        errors,
        validate,
        clearErrors,
    }
}