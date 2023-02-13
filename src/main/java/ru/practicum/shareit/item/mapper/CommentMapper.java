package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@UtilityClass
public class CommentMapper {

    public static CommentDTO toCommentDto(Comment comment) {
        CommentDTO itemDtoWithComment = new CommentDTO();
        itemDtoWithComment.setId(comment.getId());
        itemDtoWithComment.setText(comment.getText());
        itemDtoWithComment.setItemName(comment.getItem().getName());
        itemDtoWithComment.setAuthorName(comment.getAuthor().getName());
        itemDtoWithComment.setCreated(comment.getCreated());
        return itemDtoWithComment;
    }

    public static Comment toComment(CommentDTO itemDtoWithComment, Item item, User author) {
        Comment comment = new Comment();
        comment.setId(comment.getId());
        comment.setText(itemDtoWithComment.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        return comment;
    }


    public static List<CommentDTO> mapToCommentDto(Iterable<Comment> comments) {
        List<CommentDTO> dtos = new ArrayList<>();
        for (Comment comment : comments) {
            dtos.add(toCommentDto(comment));
        }
        return dtos;
    }
}
