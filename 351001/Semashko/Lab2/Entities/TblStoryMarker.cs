using System;
using System.Collections.Generic;

namespace lab_1.Entities;

public partial class TblStoryMarker:TblBase
{
    public TblStoryMarker(){}
    public long StoryId { get; set; }

    public long MarkerId { get; set; }

    public virtual TblMarker Marker { get; set; } = null!;

    public virtual TblStory Story { get; set; } = null!;
}
