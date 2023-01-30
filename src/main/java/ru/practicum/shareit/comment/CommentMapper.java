package ru.practicum.shareit.comment;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class CommentMapper {

    public static Comment toComment(CommentDTO commentDto, Item item, User user) {
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setCreated(commentDto.getCreated());
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public static CommentDTO toCommentDto(Comment comment) {
        CommentDTO commentDto = new CommentDTO();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
       // commentDto.setItem(ItemMapper.toIDto(comment.getItem()));
        commentDto.setCreated(comment.getCreated());
       // commentDto.setAuthor(UserMapper.toUserDto(comment.getAuthor()));
        commentDto.setAuthorName(comment.getAuthor().getName());
        return commentDto;
    }

    public static Set<CommentDTO> toCommentDtos(Set<Comment> comments) {
        Set<CommentDTO> dtos = new HashSet<>();
        for (Comment comment : comments) {
            dtos.add(toCommentDto(comment));
        }
        return dtos;
    }
}