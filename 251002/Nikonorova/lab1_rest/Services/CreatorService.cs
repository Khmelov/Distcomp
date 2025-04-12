using distcomp.DTOs;
using distcomp.Models;
using distcomp.Repositories;
using Microsoft.AspNetCore.Http.HttpResults;
using System.Collections.Generic;

namespace distcomp.Services
{
    public class CreatorService
    {
        private readonly CreatorRepository _creatorRepository;

        public CreatorService(CreatorRepository creatorRepository)
        {
            _creatorRepository = creatorRepository;
        }

       
        public CreatorResponseTo CreateCreator(CreatorRequestTo creatorRequest)
        {
            var creator = new Creator
            {
                Login = creatorRequest.Login,
                Password = creatorRequest.Password,
                Firstname = creatorRequest.Firstname,
                Lastname = creatorRequest.Lastname
            };

            var savedCreator = _creatorRepository.Save(creator);
            return MapToCreatorResponse(savedCreator);
        }

        
        public CreatorResponseTo GetCreatorById(long id)
        {
            var creator = _creatorRepository.FindById(id);
            return creator != null ? MapToCreatorResponse(creator) : null;
        }

        
        public List<CreatorResponseTo> GetAllCreators()
        {
            var creators = _creatorRepository.FindAll();
            var creatorResponses = new List<CreatorResponseTo>();


            foreach (var creator in creators)
            {
                creatorResponses.Add(MapToCreatorResponse(creator));
            }
            return creatorResponses;
        }


        
        public CreatorResponseTo UpdateCreator(long id, CreatorRequestTo creatorRequest)
        {
            var creator = _creatorRepository.FindById(id); //checking that user exists
            if (creator == null)
                return null;

            creator.Login = creatorRequest.Login;
            creator.Password = creatorRequest.Password;
            creator.Firstname = creatorRequest.Firstname;
            creator.Lastname = creatorRequest.Lastname;

            var updatedCreator = _creatorRepository.Update(creator);
            return MapToCreatorResponse(updatedCreator);
        }

       
        public bool DeleteCreator(long id)
        {
            return _creatorRepository.DeleteById(id);
        }

        // transforming user entity into DTO
        private CreatorResponseTo MapToCreatorResponse(Creator creator)
        {
            return new CreatorResponseTo
            {
                Id = creator.Id,
                Login = creator.Login,
                Firstname = creator.Firstname,
                Lastname = creator.Lastname
            };
        }
    }
}
