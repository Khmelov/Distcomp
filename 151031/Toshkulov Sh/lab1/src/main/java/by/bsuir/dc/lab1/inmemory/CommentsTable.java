package by.bsuir.dc.lab1.inmemory;

import by.bsuir.dc.lab1.entities.Comment;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class CommentsTable {

    private static CommentsTable instance;
    private BigInteger id = BigInteger.valueOf(1);
    private List<Comment> comments = new ArrayList<>();

    private CommentsTable(){

    }

    public Comment add(Comment comment){
        comment.setId(id);
        comments.add(comment);
        id = id.add(BigInteger.valueOf(1));
        return comment;
    }
    public boolean delete(BigInteger id){
        for(Comment comment : comments){
            if(comment.getId().equals(id)){
                return comments.remove(comment);
            }
        }
        return false;
    }
    public Comment getById(BigInteger id){
        for(Comment comment : comments){
            if(comment.getId().equals(id)){
                return comment;
            }
        }
        return null;
    }
    public List<Comment> getAll(){
        return comments;
    }

    public Comment update(Comment comment){
        Comment oldComment = getById(comment.getId());
        if(oldComment != null){
            comments.set(comments.indexOf(oldComment), comment);
            return comment;
        } else {
            return null;
        }
    }

    public static CommentsTable getInstance(){
        if(instance == null){
            instance = new CommentsTable();
        }
        return instance;
    }
}
