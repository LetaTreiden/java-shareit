package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDTOWithComment;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CommentMapper {

    public static ItemDTOWithComment toCommentDto(Comment comment) {
        ItemDTOWithComment itemDtoWithComment = new ItemDTOWithComment();
        itemDtoWithComment.setId(comment.getId());
        itemDtoWithComment.setText(comment.getText());
        itemDtoWithComment.setItemName(comment.getItem().getName());
        itemDtoWithComment.setAuthorName(comment.getAuthor().getName());
        itemDtoWithComment.setCreated(comment.getCreated());
        return itemDtoWithComment;
    }

    public static Comment toComment(ItemDTOWithComment itemDtoWithComment, Item item, User author) {
        Comment comment = new Comment();
        comment.setId(comment.getId());
        comment.setText(itemDtoWithComment.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        return comment;
    }

    public static List<ItemDTOWithComment> mapToCommentDto(Iterable<Comment> comments) {
        List<ItemDTOWithComment> dtos = new ArrayList<>();
        for (Comment comment : comments) {
            dtos.add(toCommentDto(comment));
        }
        return dtos;
    }

    public static List<Comment> toCommentShortList(Collection<Comment> comments) {
        return new ArrayList<>(comments);
    }
}
